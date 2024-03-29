﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using System.Linq;
using TMPro;
using UnityEngine.SceneManagement;
using IceCreamMakerExtensions;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class GameManager : MonoBehaviour, IManager
{
    public List<IceCreamBar> BarList;
    public TextMeshProUGUI DaytimeText;
    public TextMeshProUGUI MoneyText;
    public TextMeshProUGUI ExtraTimeText;
    public EndGameResults ResultScreen;
    public CustomerLine CustomerLine;
    public ParkColorTweener ParkColor;
    public OrderFeedback OrderFeedbackUI;
    private bool waitingForOrder = false;

    private Vector3 ExtraTimeStartPos;

    private float currHour = 8;
    private float currMin = 0;
    private float timeSpeed = 20;

    private float currentMoney = 0;
    private int perfectChainCount = 0;
    private int serveCount = 0;
    private int happiness = 0;
    private int accuracy = 0;

    private class TouchData
    {
        public int ID;
        public Vector3 StartPos;
        public Vector3 CurrPos;
        public float ElaspsedTime;
        public bool Ended;
        public TouchData(int id, Vector3 startPos)
        {
            ID = id;
            CurrPos = StartPos = startPos;
            ElaspsedTime = 0;
            Ended = false;
        }
    }
    private GraphicRaycaster m_Raycaster;
    private PointerEventData m_PointerEventData;
    private EventSystem m_EventSystem;

    private List<TouchData> touchList = new List<TouchData>();
    private bool gameOver = false;
    private int creamCounter = 0;

    // Start is called before the first frame update
    void Start()
    {
        DOTween.Sequence().AppendInterval(0.1f).AppendCallback(() =>
        {
            this.enabled = true;
        });
        this.enabled = false;
        Input.multiTouchEnabled = true;
        IceCreamResources.Instance.ManagerInterface = this;
        
        ExtraTimeStartPos = ExtraTimeText.transform.position;
        ExtraTimeText.color = ExtraTimeText.color.ChangeA(0);

        //Fetch the Raycaster from the GameObject (the Canvas)
        m_Raycaster = GameObject.Find("Canvas").GetComponent<GraphicRaycaster>();
        //Fetch the Event System from the Scene
        m_EventSystem = GameObject.Find("EventSystem").GetComponent<EventSystem>();
    }

    // Update is called once per frame
    void Update()
    {
        UpdateInputData();

        if (CustomerLine.IsEmpty())
        {
            BarList.ForEach(bar => bar.FadeOut());
            DOTween.Sequence().Append(MoneyText.DOColor(Color.green, 0.5f))
                .Append(MoneyText.DOColor(Color.white, 0.5f))
                .SetLoops(3);
            DOTween.Sequence().Append(DaytimeText.DOColor(Color.green, 0.5f))
                .Append(DaytimeText.DOColor(Color.white, 0.5f))
                .SetLoops(3).OnComplete(ShowEndScreen);
            this.enabled = false;
            waitingForOrder = false;
        }

        if (!CustomerLine.IsEmpty() && BarList.All(bar => bar.IsReadyToTakeNewOrder()))
        {
            BarList.ForEach(bar => bar.TakeInNewOrder(Random.Range(2, 7)));
            waitingForOrder = true;
        }

        foreach (var t in touchList)
        {
            if (t.Ended)
            {
                HandleTouch(t);
            }
        }

        if ( (BarList.All(bar => bar.CanServe() || bar.CantBeAddedAnymore())) && waitingForOrder)
        {
            waitingForOrder = false;
            int successCount = BarList.Count(bar => bar.CanServe());

            serveCount++;
            happiness += successCount;
            accuracy += successCount;

            BarList.ForEach(bar => bar.DoServeAnimation());
            CustomerLine.ClearCustomer(successCount);
            if(successCount == 3)
            {
                OrderFeedbackUI.DoPerfect(++perfectChainCount);
                if(perfectChainCount % 5 == 0 && !gameOver)
                {
                    ActivateFreeze();
                }
            }
            else if (successCount == 2)
            {
                perfectChainCount = 0;
                OrderFeedbackUI.DoOk();
            }
            else
            {
                perfectChainCount = 0;
                OrderFeedbackUI.DoBad();
            }
        }

        UpdateDayTime();
    }

    void UpdateDayTime()
    {
        if(gameOver)
        {
            currMin = 0;
            return;
        }
        currMin += Time.deltaTime * timeSpeed;
        if (currMin >= 60)
        {
            currMin -= 60;
            currHour++;
            if (currHour >= 21)
            {
                gameOver = true;
                //waitingForOrder = false;
                currMin = 0;
                //BarList.ForEach(bar => bar.FadeOut());
                //DOTween.Sequence().Append(MoneyText.DOColor(Color.green, 0.5f))
                //    .Append(MoneyText.DOColor(Color.white, 0.5f))
                //    .SetLoops(3);
                //DOTween.Sequence().Append(DaytimeText.DOColor(Color.green, 0.5f))
                //    .Append(DaytimeText.DOColor(Color.white, 0.5f))
                //    .SetLoops(3).OnComplete(ShowEndScreen);

                CustomerLine.DayEnd();
            }
        }
        var text = "";
        var hourToDisplay = currHour > 12 ? currHour - 12 : currHour;
        if (hourToDisplay < 10)
            text = "0" + (int)(hourToDisplay);
        else
            text = ((int)(currHour)).ToString();
        if (currMin < 10)
            text += ":0" + (int)(currMin);
        else
            text += ":" + (int)(currMin);
        if (currHour >= 12)
            text += "pm";
        else
            text += "am";
        DaytimeText.text = text;

        ParkColor.CurrentTargetPercent = (currHour-8) / (21.0f-8.0f);
    }

    void ShowEndScreen()
    {
        DaytimeText.DOFade(0, 0.4f);
        MoneyText.DOFade(0, 0.4f);
        ExtraTimeText.DOFade(0, 0.4f);

        int happinessPercent = (int)((happiness / (serveCount * 3.0f)) * 100.0f);
        happinessPercent = serveCount == 0 ? 0 : happinessPercent;
        ResultScreen.Show(serveCount, happinessPercent, happinessPercent, currentMoney);
    }

    void ResetLevel()
    {
        SceneManager.LoadScene("SampleScene");
    }
    void UpdateInputData()
    {
        touchList.RemoveAll(t => t.Ended);

        if (Input.touchCount > 0)
        {
            foreach (var touch in Input.touches)
            {
                if (touch.phase == TouchPhase.Began)
                {
                    //Set up the new Pointer Event
                    m_PointerEventData = new PointerEventData(m_EventSystem);
                    //Set the Pointer Event Position to that of the mouse position
                    m_PointerEventData.position = touch.position;

                    //Create a list of Raycast Results
                    List<RaycastResult> results = new List<RaycastResult>();

                    //Raycast using the Graphics Raycaster and mouse click position
                    m_Raycaster.Raycast(m_PointerEventData, results);

                    var debugText = touch.fingerId + " Touch begin:";

                    //For every result returned, output the name of the GameObject on the Canvas hit by the Ray
                    bool hasHit = results.Count > 0;
                    if (!hasHit)
                    {
                        debugText += " pass";
                        touchList.Add(new TouchData(touch.fingerId, touch.position));
                    }
                    else
                    {
                        debugText += " fail";
                    }
                    ScreenLogger.Log(debugText);
                }
                else if (touch.phase == TouchPhase.Moved || touch.phase == TouchPhase.Stationary)
                {
                    foreach (var t in touchList)
                    {
                        if (t.ID != touch.fingerId)
                            continue;
                        t.ElaspsedTime += Time.deltaTime;
                        t.CurrPos = touch.position;
                    }
                }
                else if (touch.phase == TouchPhase.Canceled || touch.phase == TouchPhase.Ended)
                {
                    foreach (var t in touchList)
                    {
                        if (t.ID != touch.fingerId)
                            continue;
                        t.Ended = true;
                    }
                }
            }
        }

#if UNITY_STANDALONE_WIN || UNITY_EDITOR_WIN
        if (Input.GetMouseButtonDown(0))
        {
            //Set up the new Pointer Event
            m_PointerEventData = new PointerEventData(m_EventSystem);
            //Set the Pointer Event Position to that of the mouse position
            m_PointerEventData.position = Input.mousePosition;

            //Create a list of Raycast Results
            List<RaycastResult> results = new List<RaycastResult>();

            //Raycast using the Graphics Raycaster and mouse click position
            m_Raycaster.Raycast(m_PointerEventData, results);

            //For every result returned, output the name of the GameObject on the Canvas hit by the Ray
            bool hasHit = results.Count > 0;
            if (!hasHit)
            {
                touchList.Add(new TouchData(-1, Input.mousePosition));
                //ScreenLogger.Log("Touch down:" + Input.mousePosition);
            }
        }
        else if(Input.GetMouseButton(0))
        {
            foreach(var t in touchList)
            {
                if (t.ID != -1)
                    continue;
                t.CurrPos = Input.mousePosition;
                t.ElaspsedTime += Time.deltaTime;
            }
        }
        else if(Input.GetMouseButtonUp(0))
        {
            foreach (var t in touchList)
            {
                if (t.ID != -1)
                    continue;
                t.Ended = true;
                //ScreenLogger.Log("Touch up:" + Input.mousePosition);
            }
        }
#endif
    }

    void HandleTouch(TouchData touchData)
    {
        float percentage = touchData.CurrPos.x / Screen.width;
        int sector = 0;
        if (percentage > 0.66f)
        {
            sector = 2;
        }
        else if (percentage > 0.33f)
        {
            sector = 1;
        }

        var currentFlavour = CustomerLine.GetFirstCustomerRequest();
        BarList[sector].AddScoop(currentFlavour);

        var go = GameObject.Instantiate(IceCreamResources.Instance.TapSparkPrefab);
        go.transform.position = Camera.main.ScreenToWorldPoint(touchData.CurrPos);
    }

    public void UpdateProfit(float amount, int creamCount)
    {
        var before = (int)(currentMoney / 10.0f);
        currentMoney += amount;
        var after = (int)(currentMoney / 10.0f);
        MoneyText.text = "$" + currentMoney.ToString("F2");

        creamCounter += creamCount;
        if (before != after)
        {
          //  ActivateFreeze();
        }
    }

    public void ActivateFreeze()
    {
        currHour -= 2;
        ExtraTimeText.transform.position = ExtraTimeStartPos;
        DOTween.Sequence()
            .Append(ExtraTimeText.DOFade(1, 0.2f))
            .AppendInterval(1)
            .Append(ExtraTimeText.DOFade(0, 0.8f));
        ExtraTimeText.transform.DOMoveY(50, 2).SetDelay(0.2f).SetRelative(true);
    }
}

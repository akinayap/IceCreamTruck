using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using System.Linq;
using TMPro;
using UnityEngine.SceneManagement;
using IceCreamMakerExtensions;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class GameManager : MonoBehaviour
{
    public List<IceCreamBar> BarList;
    public TextMeshProUGUI DaytimeText;
    public TextMeshProUGUI MoneyText;
    public TextMeshProUGUI ExtraTimeText;
    private bool waitingForOrder = false;
    private bool ableToFreeze = false;

    private Vector3 ExtraTimeStartPos;

    private float currHour = 8;
    private float currMin = 0;
    private float timeSpeed = 20;

    private float currentMoney = 0;

    private class TouchData
    {
        public int ID;
        public Vector3 StartPos;
        public Vector3 CurrPos;
        public float ElaspsedTime;
        public bool Ended;
        public bool Updated;
        public TouchData(int id, Vector3 startPos)
        {
            ID = id;
            CurrPos = StartPos = startPos;
            ElaspsedTime = 0;
            Ended = false;
            Updated = true;
        }

        public bool SwipedUp()
        {
            float limit = 25;
            return CurrPos.y - StartPos.y > limit;
        }
    }
    GraphicRaycaster m_Raycaster;
    PointerEventData m_PointerEventData;
    EventSystem m_EventSystem;

    List<TouchData> touchList = new List<TouchData>();
    bool gameOver = false;

    // Start is called before the first frame update
    void Start()
    {
        DOTween.Sequence().AppendInterval(0.5f).AppendCallback(() =>
        {
            this.enabled = true;
        });
        this.enabled = false;
        Input.multiTouchEnabled = true;
        IceCreamResources.Instance.Manager = this;

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

        if (!gameOver)
        {
            if (BarList.All(bar => bar.IsReadyToTakeNewOrder()))
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
        }

        UpdateDayTime();
    }

    void UpdateDayTime()
    {
        if(currHour >= 21)
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
                waitingForOrder = false;
                currMin = 0;
                BarList.ForEach(bar => bar.FadeOut());
                DOTween.Sequence().Append(MoneyText.DOColor(Color.green, 0.5f))
                    .Append(MoneyText.DOColor(Color.white, 0.5f))
                    .SetLoops(3);
                DOTween.Sequence().Append(DaytimeText.DOColor(Color.green, 0.5f))
                    .Append(DaytimeText.DOColor(Color.white, 0.5f))
                    .SetLoops(3).OnComplete(ResetLevel);
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
            foreach (var t in touchList)
            {
                t.Updated = false;
            }
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
                        t.Updated = true;
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
            {
                foreach (var t in touchList)
                {
                    if (t.Updated)
                        continue;
                    t.Ended = true;
                    var debugText = t.ID + "Touch lifted:";
                    debugText += " Removed";
                    ScreenLogger.Log(debugText);
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
                ScreenLogger.Log("Touch down:" + Input.mousePosition);
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
                ScreenLogger.Log("Touch up:" + Input.mousePosition);
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
        BarList[sector].AddScoop();

        var go = GameObject.Instantiate(IceCreamResources.Instance.TapSparkPrefab);
        go.transform.position = Camera.main.ScreenToWorldPoint(touchData.CurrPos);
    }

    public void UpdateProfit(float amount)
    {
        var before = (int)(currentMoney / 10.0f);
        currentMoney += amount;
        var after = (int)(currentMoney / 10.0f);
        MoneyText.text = "$" + currentMoney.ToString("F2");

        if(before != after)
        {
            ableToFreeze = true;
        }
    }

    public void ServePressed()
    {
        if (BarList.All(bar => bar.CanServe() || bar.HasFinishedExiting()))
        {
            BarList.ForEach(bar => bar.DoServeAnimation());
            waitingForOrder = false;
        }
    }

    public void FreezePressed()
    {
        if (!ableToFreeze)
            return;
        ableToFreeze = false;

        currHour -= 2;
        ExtraTimeText.transform.position = ExtraTimeStartPos;
        DOTween.Sequence()
            .Append(ExtraTimeText.DOFade(1, 0.2f))
            .AppendInterval(1)
            .Append(ExtraTimeText.DOFade(0, 0.8f));
        ExtraTimeText.transform.DOMoveY(50, 2).SetDelay(0.2f).SetRelative(true);
    }

    public void OnScreenPressed()
    {

    }
}

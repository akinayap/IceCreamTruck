using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using System.Linq;
using TMPro;
using UnityEngine.SceneManagement;

public class GameManager : MonoBehaviour
{
    public List<IceCreamBar> BarList;
    public TextMeshProUGUI TimeText;
    public TextMeshProUGUI DaytimeText;
    public TextMeshProUGUI MoneyText;
    private bool waitingForOrder = false;

    private float currHour = 8;
    private float currMin = 0;
    private float timeSpeed = 30;

    private float currentMoney = 0;

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

        public bool SwipedUp()
        {
            float limit = 25;
            return CurrPos.y - StartPos.y > limit;
        }
    }

    List<TouchData> touchList = new List<TouchData>();
    float orderTime;
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
                orderTime = 0;
                waitingForOrder = true;
            }

            foreach (var t in touchList)
            {
                if (t.SwipedUp())
                {
                    if (BarList.All(bar => bar.CanServe() || bar.HasFinishedExiting()))
                    {
                        BarList.ForEach(bar => bar.DoServeAnimation());
                        t.Ended = true;
                        waitingForOrder = false;
                    }
                }
                else if (t.Ended)
                {
                    HandleTouch(t);
                }
            }
        }

        if(waitingForOrder)
        {
            TimeText.text = "Order Time: " + orderTime.ToString("F2") + "s";
            orderTime += Time.deltaTime;
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
                TimeText.DOFade(0, 0.5f);
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
            foreach (var touch in Input.touches)
            {
                if (touch.phase == TouchPhase.Began)
                {
                    touchList.Add(new TouchData(touch.fingerId, touch.position));
                }
                else if (touch.phase == TouchPhase.Moved)
                {
                    foreach (var t in touchList)
                    {
                        if (t.ID != touch.fingerId)
                            continue;
                        t.ElaspsedTime += Time.deltaTime;
                        t.CurrPos = touch.position;
                    }
                }
                else if (touch.phase == TouchPhase.Ended)
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
            touchList.Add(new TouchData(-1, Input.mousePosition));
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
    }

    public void UpdateProfit(float amount)
    {
        currentMoney += amount;
        MoneyText.text = "$" + currentMoney.ToString("F2");
    }
}

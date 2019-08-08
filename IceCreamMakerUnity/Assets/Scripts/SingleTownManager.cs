using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using TMPro;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using IceCreamMakerExtensions;

public class SingleTownManager : MonoBehaviour {

    public SingleTownIceCreamBar Bar;
    public TextMeshProUGUI DaytimeText;
    public TextMeshProUGUI MoneyText;
    public TextMeshProUGUI ExtraTimeText;
    public Button ServeButton;
    public List<SpriteRenderer> Flavours;
    public EndGameResults ResultScreen;

//    private bool waitingForOrder = false;

//    private Vector3 ExtraTimeStartPos;

//    private float currHour = 8;
//    private float currMin = 0;
//    private float timeSpeed = 30;

//    private float currentMoney = 0;

//    private List<TouchData> touchList = new List<TouchData>();
//    private bool gameOver = false;
//    private int creamCounter = 0;

//    private class TouchData
//    {
//        public int ID;
//        public Vector3 StartPos;
//        public Vector3 CurrPos;
//        public float ElaspsedTime;
//        public bool Ended;
//        public TouchData(int id, Vector3 startPos)
//        {
//            ID = id;
//            CurrPos = StartPos = startPos;
//            ElaspsedTime = 0;
//            Ended = false;
//        }

//        public bool SwipedUp()
//        {
//            float limit = 25;
//            return CurrPos.y - StartPos.y > limit;
//        }
//    }
//    private GraphicRaycaster m_Raycaster;
//    private PointerEventData m_PointerEventData;
//    private EventSystem m_EventSystem;


//    // Use this for initialization
//    void Start () {

//        DOTween.Sequence().AppendInterval(2.5f).AppendCallback(() =>
//        {
//            this.enabled = true;
//        });
//        this.enabled = false;
//        Input.multiTouchEnabled = true;
//        IceCreamResources.Instance.ManagerInterface = this;

//        ExtraTimeStartPos = ExtraTimeText.transform.position;
//        ExtraTimeText.color = ExtraTimeText.color.ChangeA(0);

//        for (int i = 0; i < Flavours.Count; ++i)
//        {
//            var s = Flavours[i];
//            s.color = s.color.ChangeA(0);
//            s.DOFade(1, 0.5f).OnComplete(() => s.DOFade(0.3f, 0.5f).SetDelay(0.2f)).SetDelay(i * 0.8f);
//        }

//        //Fetch the Raycaster from the GameObject (the Canvas)
//        m_Raycaster = GameObject.Find("Canvas").GetComponent<GraphicRaycaster>();
//        //Fetch the Event System from the Scene
//        m_EventSystem = GameObject.Find("EventSystem").GetComponent<EventSystem>();
//    }
	
//	// Update is called once per frame
//	void Update () {

//        UpdateInputData();

//        if (!gameOver)
//        {
//            if (Bar.IsReadyToTakeNewOrder())
//            {
//                Bar.TakeInNewOrder(Random.Range(5, 8));
//                waitingForOrder = true;
//            }

//            foreach (var t in touchList)
//            {
//                if (t.Ended)
//                {
//                    HandleTouch(t);
//                }
//            }
//        }

//        if (Bar.CanServe() || Bar.CantBeAddedAnymore())
//        {
//            ServeButton.GetComponent<Image>().color = Color.white;
//        }
//        else
//        {
//            ServeButton.GetComponent<Image>().color = Color.gray;
//        }

//        UpdateDayTime();
//    }

//    void UpdateInputData()
//    {
//        touchList.RemoveAll(t => t.Ended);

//        if (Input.touchCount > 0)
//        {
//            foreach (var touch in Input.touches)
//            {
//                if (touch.phase == TouchPhase.Began)
//                {
//                    //Set up the new Pointer Event
//                    m_PointerEventData = new PointerEventData(m_EventSystem);
//                    //Set the Pointer Event Position to that of the mouse position
//                    m_PointerEventData.position = touch.position;

//                    //Create a list of Raycast Results
//                    List<RaycastResult> results = new List<RaycastResult>();

//                    //Raycast using the Graphics Raycaster and mouse click position
//                    m_Raycaster.Raycast(m_PointerEventData, results);

//                    var debugText = touch.fingerId + " Touch begin:";

//                    //For every result returned, output the name of the GameObject on the Canvas hit by the Ray
//                    bool hasHit = results.Count > 0;
//                    if (!hasHit)
//                    {
//                        debugText += " pass";
//                        touchList.Add(new TouchData(touch.fingerId, touch.position));
//                    }
//                    else
//                    {
//                        debugText += " fail";
//                    }
//                    ScreenLogger.Log(debugText);
//                }
//                else if (touch.phase == TouchPhase.Moved || touch.phase == TouchPhase.Stationary)
//                {
//                    foreach (var t in touchList)
//                    {
//                        if (t.ID != touch.fingerId)
//                            continue;
//                        t.ElaspsedTime += Time.deltaTime;
//                        t.CurrPos = touch.position;
//                    }
//                }
//                else if (touch.phase == TouchPhase.Canceled || touch.phase == TouchPhase.Ended)
//                {
//                    foreach (var t in touchList)
//                    {
//                        if (t.ID != touch.fingerId)
//                            continue;
//                        t.Ended = true;
//                    }
//                }
//            }
//        }

//#if UNITY_STANDALONE_WIN || UNITY_EDITOR_WIN
//        if (Input.GetMouseButtonDown(0))
//        {
//            //Set up the new Pointer Event
//            m_PointerEventData = new PointerEventData(m_EventSystem);
//            //Set the Pointer Event Position to that of the mouse position
//            m_PointerEventData.position = Input.mousePosition;

//            //Create a list of Raycast Results
//            List<RaycastResult> results = new List<RaycastResult>();

//            //Raycast using the Graphics Raycaster and mouse click position
//            m_Raycaster.Raycast(m_PointerEventData, results);

//            //For every result returned, output the name of the GameObject on the Canvas hit by the Ray
//            bool hasHit = results.Count > 0;
//            if (!hasHit)
//            {
//                touchList.Add(new TouchData(-1, Input.mousePosition));
//                //ScreenLogger.Log("Touch down:" + Input.mousePosition);
//            }
//        }
//        else if (Input.GetMouseButton(0))
//        {
//            foreach (var t in touchList)
//            {
//                if (t.ID != -1)
//                    continue;
//                t.CurrPos = Input.mousePosition;
//                t.ElaspsedTime += Time.deltaTime;
//            }
//        }
//        else if (Input.GetMouseButtonUp(0))
//        {
//            foreach (var t in touchList)
//            {
//                if (t.ID != -1)
//                    continue;
//                t.Ended = true;
//                //ScreenLogger.Log("Touch up:" + Input.mousePosition);
//            }
//        }
//#endif
//    }

//    void HandleTouch(TouchData touchData)
//    {
//        RaycastHit2D rayHit = Physics2D.Raycast(Camera.main.ScreenToWorldPoint(touchData.CurrPos), Vector2.zero);
//        Bar.AddScoop(rayHit.collider.gameObject.name);

//        var go = GameObject.Instantiate(IceCreamResources.Instance.TapSparkPrefab);
//        go.transform.position = Camera.main.ScreenToWorldPoint(touchData.CurrPos);
//    }
//    void UpdateDayTime()
//    {
//        if (currHour >= 21)
//        {
//            currMin = 0;
//            return;
//        }
//        currMin += Time.deltaTime * timeSpeed;
//        if (currMin >= 60)
//        {
//            currMin -= 60;
//            currHour++;
//            if (currHour >= 21)
//            {
//                gameOver = true;
//                waitingForOrder = false;
//                currMin = 0;
//                Bar.FadeOut();
//                DOTween.Sequence().Append(MoneyText.DOColor(Color.green, 0.5f))
//                    .Append(MoneyText.DOColor(Color.white, 0.5f))
//                    .SetLoops(3);
//                DOTween.Sequence().Append(DaytimeText.DOColor(Color.green, 0.5f))
//                    .Append(DaytimeText.DOColor(Color.white, 0.5f))
//                    .SetLoops(3).OnComplete(ShowEndScreen);
//            }
//        }
//        var text = "";
//        var hourToDisplay = currHour > 12 ? currHour - 12 : currHour;
//        if (hourToDisplay < 10)
//            text = "0" + (int)(hourToDisplay);
//        else
//            text = ((int)(currHour)).ToString();
//        if (currMin < 10)
//            text += ":0" + (int)(currMin);
//        else
//            text += ":" + (int)(currMin);
//        if (currHour >= 12)
//            text += "pm";
//        else
//            text += "am";
//        DaytimeText.text = text;
//    }

//    public void UpdateProfit(float cents, int count)
//    {
//        currentMoney += cents;
//        MoneyText.text = "$" + currentMoney.ToString("F2");

//        var before = creamCounter / 20;
//        creamCounter += count;
//        var after = creamCounter / 20;
//        if (before != after)
//        {
//            ActivateFreeze();
//        }
//    }
//    void ShowEndScreen()
//    {
//        DaytimeText.DOFade(0, 0.4f);
//        MoneyText.DOFade(0, 0.4f);
//        ExtraTimeText.DOFade(0, 0.4f);
//        ServeButton.GetComponent<Image>().DOFade(0, 0.4f);
//       // ResultScreen.Show();
//    }
//    public void ServePressed()
//    {
//        if(Bar.CanServe() || Bar.CantBeAddedAnymore())
//        {
//            Bar.DoServeAnimation();
//            waitingForOrder = false;
//        }
//    }
//    public void ActivateFreeze()
//    {
//        currHour -= 2;
//        ExtraTimeText.transform.position = ExtraTimeStartPos;
//        DOTween.Sequence()
//            .Append(ExtraTimeText.DOFade(1, 0.2f))
//            .AppendInterval(1)
//            .Append(ExtraTimeText.DOFade(0, 0.8f));
//        ExtraTimeText.transform.DOMoveY(50, 2).SetDelay(0.2f).SetRelative(true);
//    }
}

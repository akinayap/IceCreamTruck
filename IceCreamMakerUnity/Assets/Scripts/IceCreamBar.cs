using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using IceCreamMakerExtensions;
using DG.Tweening;

public class IceCreamBar : MonoBehaviour
{
    private SpriteRenderer cone;
    private SpriteRenderer cream1;
    private SpriteRenderer cream2;
    private float distBetweenCream;
    private Vector3 coneStartPos;

    private List<SpriteRenderer> dottedList = new List<SpriteRenderer>();
    private List<SpriteRenderer> creamList = new List<SpriteRenderer>();

    private enum State
    {
        WaitingToFinish,
        Match,
        Exiting,
        FinishedExiting,
        Reseting,
        ReadyForOrder
    }
    private State currentState = State.ReadyForOrder;

    // Start is called before the first frame update
    void Start()
    {
        cone = transform.Find("cone").GetComponent<SpriteRenderer>();
        cream1 = transform.Find("cream1").GetComponent<SpriteRenderer>();
        cream2 = transform.Find("cream2").GetComponent<SpriteRenderer>();
        distBetweenCream = cream2.transform.position.y - cream1.transform.position.y;
        coneStartPos = cone.transform.position;

        cone.gameObject.SetActive(false);
        cream1.gameObject.SetActive(false);
        cream2.gameObject.SetActive(false);

        IceCreamResources.Load();
    }

    public void TakeInNewOrder(int count)
    {
        currentState = State.WaitingToFinish;

        var delayPerCream = 0.2f;
        var fadeInTimePerCream = 0.5f;

        cone.gameObject.SetActive(true);
        cone.color = cone.color.ChangeA(0);
        cone.DOFade(1, fadeInTimePerCream);

        var basePos = this.cream1.transform.position;

        for (int i=0;i<count;++i)
        {
            var dottedCream = GameObject.Instantiate(IceCreamResources.Instance.DottedPrefab);
            var dottedPos = new Vector3(basePos.x, basePos.y + distBetweenCream*i, basePos.z);
            dottedCream.transform.position = dottedPos;

            var originalScale = dottedCream.transform.localScale;
            dottedCream.transform.localScale = new Vector3(0.001f, 0.001f, 0.001f);
            dottedCream.transform.DOScale(originalScale, fadeInTimePerCream).SetEase(Ease.InOutBounce).SetDelay(i * delayPerCream);

            var dottedSprite = dottedCream.GetComponent<SpriteRenderer>();
            dottedSprite.color = dottedSprite.color.ChangeA(0);
            dottedSprite.DOFade(1, fadeInTimePerCream).SetDelay(i* delayPerCream);

            dottedList.Add(dottedSprite);
        }
    }

    public void AddScoop()
    {
        if (dottedList.Count < creamList.Count)
        {
            return;
        }
        else if (dottedList.Count == creamList.Count)
        {
            var iceCream = GameObject.Instantiate(IceCreamResources.Instance.CreamPrefab);
            var fallSpeed = 0.05f;

            var basePos = cream1.transform.position;
            var creamPos = basePos.ChangeY(basePos.y + distBetweenCream * 10);
            creamPos.z = basePos.z - 0.01f * creamList.Count;

            var finalY = basePos.y + distBetweenCream * creamList.Count;
            iceCream.transform.position = creamPos;
            iceCream.transform.DOMoveY(finalY, Mathf.Abs(finalY - creamPos.y) * fallSpeed).SetEase(Ease.InOutSine).OnComplete(MakeIceCreamFall);

            var iceCreamSprite = iceCream.GetComponent<SpriteRenderer>();
            iceCreamSprite.sprite = IceCreamResources.Instance.IceCreamFlavours.RandomSprite();
            creamList.Add(iceCreamSprite);

            currentState = State.Exiting;
        }
        else
        {
            var iceCream = GameObject.Instantiate(IceCreamResources.Instance.CreamPrefab);
            var fallSpeed = 0.05f;

            var basePos = cream1.transform.position;
            var creamPos = basePos.ChangeY(basePos.y + distBetweenCream * 10);
            creamPos.z = basePos.z - 0.01f * creamList.Count;

            var finalY = basePos.y + distBetweenCream * creamList.Count;
            var timeToFall = Mathf.Abs(finalY - creamPos.y) * fallSpeed;
            iceCream.transform.position = creamPos;
            iceCream.transform.DOMoveY(finalY, timeToFall).SetEase(Ease.InOutSine);

            var iceCreamSprite = iceCream.GetComponent<SpriteRenderer>();
            iceCreamSprite.sprite = IceCreamResources.Instance.IceCreamFlavours.RandomSprite();
            creamList.Add(iceCreamSprite);

            var currentCount = creamList.Count;
            DOTween.Sequence().AppendInterval(timeToFall).AppendCallback(()=>
            {
                if(currentCount == creamList.Count)
                {
                    currentState = currentCount == dottedList.Count ? State.Match : State.WaitingToFinish;
                }
            });
        }
    }

    void MakeIceCreamFall()
    {
        foreach(var dotted in dottedList)
        {
            dotted.gameObject.SetActive(false);
        }

        foreach(var cream in creamList)
        {
            cream.transform.SetParent(cone.transform);
            cream.DOFade(0, 1);
        }
        cone.transform.DOJump(cone.transform.position.ChangeY(cone.transform.position.y - 10), 8, 1, 1);
        cone.transform.DORotate(new Vector3(0, 0, 45), 2).OnComplete(Reset);
        cone.DOFade(0, 1);
    }

    private void Reset()
    {
        foreach (var cream in creamList)
        {
            GameObject.Destroy(cream.gameObject);
        }
        creamList.Clear();

        foreach (var dotted in dottedList)
        {
            GameObject.Destroy(dotted.gameObject);
        }
        dottedList.Clear();

        cone.transform.position = coneStartPos;
        cone.transform.rotation = Quaternion.identity;

        currentState = State.FinishedExiting;
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public bool IsReadyToTakeNewOrder()
    {
        return currentState == State.FinishedExiting;
    }

    public bool CanServe()
    {
        return currentState == State.Match;
    }
    public bool HasFinishedExiting()
    {
        return currentState == State.FinishedExiting;
    }

    public void DoServeAnimation()
    {
        foreach (var dotted in dottedList)
        {
            dotted.gameObject.SetActive(false);
        }

        foreach (var cream in creamList)
        {
            cream.transform.SetParent(cone.transform);
            cream.DOFade(0, 1);
        }
        cone.transform.DOMoveX(10, 1).SetRelative(true);
        cone.DOFade(0, 1).OnComplete(Reset);

        currentState = State.Exiting;
    }
}

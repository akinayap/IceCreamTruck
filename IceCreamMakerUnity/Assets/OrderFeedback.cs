using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using DG.Tweening;
using IceCreamMakerExtensions;

public class OrderFeedback : MonoBehaviour {

    public bool Flag = false;
    public Color MistakeColor;
    public Color OkColor;

    private Vector3 startPos;
    private Vector3 startScale;

    private TextMeshProUGUI feedbackText;
    private TextMeshProUGUI feedbackCounterText;

    private float colorHVal;
    private Tween colorHSVTweener;
    // Use this for initialization
    void Start () {
        startPos = this.transform.localPosition;
        startScale = this.transform.localScale;

        feedbackText = this.transform.GetComponent<TextMeshProUGUI>();
        feedbackCounterText = this.transform.GetChild(0).GetComponent<TextMeshProUGUI>();

        feedbackText.color = feedbackText.color.ChangeA(0);
        feedbackCounterText.color = feedbackCounterText.color.ChangeA(0);

        colorHSVTweener = DOTween.To(() => colorHVal, x =>
        {
            colorHVal = x;
            feedbackCounterText.color = Color.HSVToRGB(colorHVal, 0.8f, 1).ChangeA(feedbackCounterText.color.a);
        }, 1, 0.5f).SetLoops(-1);
    }

    private void OnDestroy()
    {
        colorHSVTweener?.Kill();
    }
    
    // Update is called once per frame
    void Update () {
		if(Flag)
        {
            DoOk();
            Flag = false;
        }
    }

    public void DoBad()
    {
        this.transform.localPosition = startPos;
        feedbackText.DOFade(1, 0.5f);
        feedbackText.color = MistakeColor;
        feedbackText.text = "Mistake!";

        this.transform.localScale = Vector3.zero;
        this.transform.DOScale(this.startScale, 0.5f).SetEase(Ease.InOutBounce).OnComplete(() =>
        {
            feedbackText.DOFade(0, 1f);
            this.transform.DOMoveY(-10, 1.2f).SetRelative(true);
        });
    }

    public void DoOk()
    {
        this.transform.localPosition = startPos;
        feedbackText.DOFade(1, 0.5f);
        feedbackText.color = OkColor;
        feedbackText.text = "So so...";

        this.transform.localScale = Vector3.zero;
        this.transform.DOScale(this.startScale, 0.5f).SetEase(Ease.InOutBounce).OnComplete(() =>
        {
            feedbackText.DOFade(0, 1f);
            this.transform.DOMoveX(15, 1.2f).SetRelative(true);
        });
    }

    public void DoPerfect(int perfectCount)
    {
        this.transform.localPosition = startPos;
        feedbackText.DOFade(1, 0.5f);
        feedbackText.color = Color.white;
        feedbackText.text = "Perfect!";

        feedbackCounterText.DOFade(1, 0.5f);
        feedbackCounterText.text = "x" + perfectCount;
        this.transform.localScale = Vector3.zero;
        this.transform.DOScale(this.startScale, 0.5f).SetEase(Ease.InOutBounce).OnComplete(() =>
        {
            feedbackText.DOFade(0, 1f);
            feedbackCounterText.DOFade(0, 1f);
            this.transform.DOMoveY(50, 1f).SetRelative(true);
        });
    }
}

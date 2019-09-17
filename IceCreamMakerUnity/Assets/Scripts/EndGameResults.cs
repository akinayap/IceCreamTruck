using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using DG.Tweening;
using IceCreamMakerExtensions;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class EndGameResults : MonoBehaviour {

    public Transform Background;
    public List<Image> FadeOutList;
    public TextMeshProUGUI CustomersServed;
    public TextMeshProUGUI Happiness;
    public TextMeshProUGUI Accuracy;
    public TextMeshProUGUI Earning;

    private bool isShowing = false;
    private bool updateText = false;

    private float currentCustomer = 0;
    private int targetCustomer;

    private float currentHappiness = 0;
    private int targetHappiness;

    private float currentAccuracy = 0;
    private int targetAccuracy;

    private float currentEarning = 0;
    private float targetEarning;

    private bool canReturn = false;

    // Use this for initialization
    void Start () {
        isShowing = false;
    }

    // Update is called once per frame
    void Update () {
        if (!isShowing)
            return;
        if(canReturn && Input.anyKey)
        {
            ResetLevel();
        }
    }

    public void Show(int serveCount, int happiness, int accuracy, float earning)
    {
        isShowing = true;
        Background.DOLocalMoveY(-6, 2).SetEase(Ease.OutBounce).OnComplete(() => ShowStats());   
        foreach(var img in FadeOutList)
        {
            img.DOFade(0,1);
        }

        targetCustomer = serveCount;
        targetHappiness = happiness;
        targetAccuracy = accuracy;
        targetEarning = earning;
    }

    private void ShowStats()
    {
        CustomersServed.DOFade(1, 1);
        DOTween.To(() => currentCustomer, x =>
        {
            currentCustomer = x;
            CustomersServed.text = "Customers Served\n" + (int)currentCustomer;
        }, targetCustomer, 2).SetDelay(0.5f);

        Happiness.DOFade(1, 1).SetDelay(0.5f);
        DOTween.To(() => currentHappiness, x =>
        {
            currentHappiness = x;
            Happiness.text = "Happiness\n" + (int)currentHappiness;
        }, (float)targetHappiness, 2).SetDelay(1f);

        Accuracy.DOFade(1, 1).SetDelay(1f);
        DOTween.To(() => currentAccuracy, x =>
        {
            currentAccuracy = x;
            Accuracy.text = "Accuracy\n" + (int)currentAccuracy;
        }, (float)targetAccuracy, 2).SetDelay(1.5f);

        Earning.DOFade(1, 1).SetDelay(1.5f);
        DOTween.To(() => currentEarning, x =>
        {
            currentEarning = x;
            Earning.text = "Earnings\n$" + currentEarning.ToString("F2");
        }, targetEarning, 2).SetDelay(2).OnComplete(()=>canReturn=true);
    }


    void ResetLevel()
    {
        BetweenScenesData.Instance.Coin += (int)currentEarning;
        SceneManager.LoadScene("Gallery");
    }
}

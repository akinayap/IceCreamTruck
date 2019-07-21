using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using DG.Tweening;
using IceCreamMakerExtensions;
using UnityEngine.SceneManagement;

public class EndGameResults : MonoBehaviour {

    public GameManager Manager;
    public TextMeshProUGUI HeaderText;
    public TextMeshProUGUI ChocoText;
    public TextMeshProUGUI VanillaText;
    public TextMeshProUGUI StrawberryText;

    private bool isShowing = false;
    private bool updateText = false;
    private float chocoCount = 0;
    private float vanillaCount = 0;
    private float strawberryCount = 0;

    private List<int> scoopCounter = new List<int>();
    public static List<int> scoopSceneCounter;
    public static List<float> scoopMultipler;

    private Sequence resetSeq;

    // Use this for initialization
    void Start () {
        isShowing = false;
        HeaderText.color = HeaderText.color.ChangeA(0);
        ChocoText.color = ChocoText.color.ChangeA(0);
        VanillaText.color = VanillaText.color.ChangeA(0);
        StrawberryText.color = StrawberryText.color.ChangeA(0);

        if(scoopSceneCounter == null || scoopSceneCounter.Count == 0)
        {
            scoopSceneCounter = new List<int>();
            scoopMultipler = new List<float>();
            for (int i = 0; i < IceCreamResources.Instance.IceCreamFlavourNames.Count; ++i)
            {
                scoopSceneCounter.Add(0);
                scoopMultipler.Add(1);
            }
        }
        else
        {
            chocoCount = scoopSceneCounter[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("chocolate")];
            vanillaCount = scoopSceneCounter[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("vanilla")];
            strawberryCount = scoopSceneCounter[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("strawberry")];
        }
        int currLevel = ((int)chocoCount) / 10;
        ChocoText.text = "Chocolate          " + (int)chocoCount + "          " + ((currLevel + 1) * 10) + "(lvl " + (currLevel + 1) + ")";
        scoopMultipler[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("chocolate")] = 1 + 0.05f * currLevel;

        currLevel = ((int)vanillaCount) / 10;
        VanillaText.text = "Vanilla          " + (int)vanillaCount + "          " + ((currLevel + 1) * 10) + "(lvl " + (currLevel + 1) + ")";
        scoopMultipler[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("vanilla")] = 1 + 0.05f * currLevel;

        currLevel = ((int)strawberryCount) / 10;
        StrawberryText.text = "Strawberry          " + (int)strawberryCount + "          " + ((currLevel + 1) * 10) + "(lvl " + (currLevel + 1) + ")";
        scoopMultipler[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("strawberry")] = 1 + 0.05f * currLevel;

    }

    // Update is called once per frame
    void Update () {
        if (!isShowing)
            return;
        if(updateText)
        {
            int currLevel = ((int)chocoCount) / 10;
            ChocoText.text = "Chocolate          " + (int)chocoCount + "          " + ((currLevel + 1) * 10) + "(lvl " + (currLevel + 1) + ")";

            currLevel = ((int)vanillaCount) / 10;
            VanillaText.text = "Vanilla          " + (int)vanillaCount + "          " + ((currLevel + 1) * 10) + "(lvl " + (currLevel + 1) + ")";

            currLevel = ((int)strawberryCount) / 10;
            StrawberryText.text = "Strawberry          " + (int)strawberryCount + "          " + ((currLevel + 1) * 10) + "(lvl " + (currLevel + 1) + ")";

            if(Input.anyKey)
            {
                resetSeq.Kill();
                ResetLevel();
            }
        }
	}

    public void Show()
    {
        isShowing = true;

        HeaderText.DOFade(1, 1);
        ChocoText.DOFade(1, 1);
        VanillaText.DOFade(1, 1);
        StrawberryText.DOFade(1, 1);

        DOTween.Sequence().AppendInterval(0.8f).AppendCallback(TweenCounters);
    }

    void TweenCounters()
    {
        updateText = true;
        scoopCounter = new List<int>();
        for(int i=0;i<IceCreamResources.Instance.IceCreamFlavourNames.Count;++i)
        {
            scoopCounter.Add(0);
        }
        foreach(var bar in Manager.BarList)
        {
            for (int i = 0; i < IceCreamResources.Instance.IceCreamFlavourNames.Count; ++i)
            {
                scoopCounter[i] += bar.ScoopCount[i];
            }
        }

        for (int i = 0; i < IceCreamResources.Instance.IceCreamFlavourNames.Count; ++i)
        {
            scoopSceneCounter[i] += scoopCounter[i];
        }

        resetSeq = DOTween.Sequence().AppendInterval(15).AppendCallback(ResetLevel);

        DOTween.To(() => chocoCount, x => chocoCount = x, scoopSceneCounter[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("chocolate")], 5);
        DOTween.To(() => vanillaCount, x => vanillaCount = x, scoopSceneCounter[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("vanilla")], 5);
        DOTween.To(() => strawberryCount, x => strawberryCount = x, scoopSceneCounter[IceCreamResources.Instance.IceCreamFlavourNames.IndexOf("strawberry")], 5);
    }

    void ResetLevel()
    {
        SceneManager.LoadScene("SampleScene");
    }
}

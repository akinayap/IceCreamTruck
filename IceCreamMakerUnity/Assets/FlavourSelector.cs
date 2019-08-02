using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using IceCreamMakerExtensions;
using UnityEngine.SceneManagement;

public class FlavourSelector : MonoBehaviour {

    public Transform ScrollView;
    public Vector3 ScrollViewCenterPos;
    public List<Button> ButtonList;
    public Button StartButton;

    private Vector3 scrollViewStartPos;
    private Button currentButton;

    private List<Button> OkList = new List<Button>();

	// Use this for initialization
	void Start () {
        scrollViewStartPos = ScrollView.localPosition;
    }
	
	// Update is called once per frame
	void Update () {
		
	}

    public void OnSelectPressed(Button pressedButton)
    {
        float moveInTime = 0.7f;
        ScrollView.DOLocalMove(ScrollViewCenterPos, moveInTime);

        var deltaX = ScrollViewCenterPos.x - ScrollView.localPosition.x;
        foreach (var b in ButtonList)
        {
            b.transform.DOLocalMoveX(deltaX, moveInTime).SetRelative(true);
        }
        currentButton = pressedButton;
    }

    public void OnFlavourPressed(Button pressedButton)
    {
        float moveOutTime = 0.7f;
        ScrollView.DOLocalMove(scrollViewStartPos, moveOutTime);

        currentButton.transform.GetChild(0).GetComponent<Image>().sprite = pressedButton.transform.Find("Image").GetComponent<Image>().sprite;

        var deltaX = scrollViewStartPos.x - ScrollView.localPosition.x;
        foreach (var b in ButtonList)
        {
            b.transform.DOLocalMoveX(deltaX, moveOutTime).SetRelative(true);
        }

        if(!OkList.Contains(currentButton))
        {
            OkList.Add(currentButton);
            if (OkList.Count == 3)
            {
                StartButton.interactable = true;
                StartButton.GetComponent<Image>().DOFade(1, 0.7f).SetDelay(0.5f);
            }
        }
    }

    public void StartDayPressed()
    {
        float moveOutTime = 0.7f;
        var deltaX = scrollViewStartPos.x - ScrollViewCenterPos.x;
        foreach (var b in ButtonList)
        {
            b.transform.DOLocalMoveX(deltaX, moveOutTime).SetRelative(true);
        }

        IceCreamResources.Instance.ChosenFlavours.Clear();
        for (int i=0;i<3;++i)
        {
            IceCreamResources.Instance.ChosenFlavours.Add(ButtonList[i].transform.GetChild(0).GetComponent<Image>().sprite);
        }

        DOTween.Sequence().AppendInterval(0.7f).OnComplete(() =>
        {
            SceneManager.LoadScene("SampleScene");
        });
    }
}

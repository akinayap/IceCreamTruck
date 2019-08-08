using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using DG.Tweening;
using IceCreamMakerExtensions;
using UnityEngine.SceneManagement;
using System.Linq;

public class FlavourSelector : MonoBehaviour {

    public Transform ScrollView;
    public Vector3 ScrollViewCenterPos;
    public List<Button> ButtonList;
    public Button StartButton;
    public Button FlavourTopLeft;
    public Button FlavourTopLeftOffsetRight;
    public Button FlavourTopLeftOffsetBtm;

    private Vector3 scrollViewStartPos;
    private Button currentButton;

    private List<Button> OkList = new List<Button>();

    class FlavourButton
    {
        public CustomersAndFlavours.Flavour flavour;
        public Button button;
        public Image image;
        public Text name;
        public Text count;
    };

    private List<FlavourButton> flavourButtons = new List<FlavourButton>();

	// Use this for initialization
	void Start () {
        scrollViewStartPos = ScrollView.localPosition;
        CustomersAndFlavours.GetInstance();
        SetupFlavours();
    }
	
	// Update is called once per frame
	void Update () {
		
	}

    void SetupFlavours()
    {
        float xDiff = FlavourTopLeftOffsetRight.transform.position.x - FlavourTopLeft.transform.position.x;
        float yDiff = FlavourTopLeftOffsetBtm.transform.position.y - FlavourTopLeft.transform.position.y;

        int flavourCount = 0;
        var dottedSprite = IceCreamResources.Instance.DottedPrefab.GetComponent<SpriteRenderer>().sprite;
        foreach (var flavour in CustomersAndFlavours.Instance.allFlavours)
        {
            var newButton = GameObject.Instantiate(FlavourTopLeft, FlavourTopLeft.transform.parent,true);

            var pos = newButton.transform.position;
            pos.x += (flavourCount % 3) * xDiff;
            pos.y += (flavourCount / 3) * yDiff;
            newButton.transform.position = pos;

            var flavourButton = new FlavourButton();
            flavourButton.flavour = flavour;
            flavourButton.button = newButton.GetComponent<Button>();
            flavourButton.image = newButton.transform.Find("Image").GetComponent<Image>();
            flavourButton.count = newButton.transform.Find("Count").GetComponent<Text>();
            flavourButton.name = newButton.transform.Find("Text").GetComponent<Text>();
            
            if (flavour.unlocked)
            {
                var flavourName = flavour.name.First().ToString().ToUpper() + flavour.name.Substring(1);
                flavourButton.name.text = flavourName;
                
                flavourButton.image.sprite = flavour.sprite;
                flavourButton.count.text = "x" + flavour.own_count.ToString();

                if (flavour.own_count <= 0)
                {
                    flavourButton.image.color *= 0.3f;
                    flavourButton.button.enabled = false;
                }
            }
            else
            {
                flavourButton.count.text = "";
                flavourButton.name.text = "";

                flavourButton.image.sprite = dottedSprite;
                flavourButton.button.GetComponent<Button>().enabled = false;
            }
            flavourButtons.Add(flavourButton);
            flavourCount++;
        }
        FlavourTopLeft.gameObject.SetActive(false);
        FlavourTopLeftOffsetRight.gameObject.SetActive(false);
        FlavourTopLeftOffsetBtm.gameObject.SetActive(false);
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

    private void UpdateCount(FlavourButton flavourButton)
    {
        Debug.Log(flavourButton);
        Debug.Log(flavourButton.flavour);
        int count = flavourButton.flavour.own_count;
        BetweenScenesData.Instance.FlavoursToUse.ForEach(f => { if (f.index == flavourButton.flavour.index) { count--; } });
        
        flavourButton.count.text = "x" + count;

        if (count <= 0)
        {
            flavourButton.image.color *= 0.3f;
            flavourButton.button.enabled = false;
        }
        else
        {
            flavourButton.image.color = Color.white;
            flavourButton.button.enabled = true;
        }
    }

    public void OnFlavourPressed(Button pressedButton)
    {
        float moveOutTime = 0.7f;
        ScrollView.DOLocalMove(scrollViewStartPos, moveOutTime);

        var flavourButton = flavourButtons.Find(f => f.button == pressedButton);
        BetweenScenesData.Instance.FlavoursToUse.Add(flavourButton.flavour);
        UpdateCount(flavourButton);

        var oldFlavourButton = flavourButtons.Find(f => f.flavour.sprite == currentButton.transform.GetChild(0).GetComponent<Image>().sprite);
        if (oldFlavourButton != null)
        {
            BetweenScenesData.Instance.FlavoursToUse.Remove(oldFlavourButton.flavour);
            UpdateCount(oldFlavourButton);
        }

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

        //BetweenScenesData.Instance.FlavoursToUse.Clear();
        //for (int i=0;i<3;++i)
        //{
        //    var flavour = CustomersAndFlavours.Instance.allFlavours.Find(f => f.sprite == ButtonList[i].transform.GetChild(0).GetComponent<Image>().sprite);
        //    BetweenScenesData.Instance.FlavoursToUse.Add(flavour);
        //}

        DOTween.Sequence().AppendInterval(0.7f).OnComplete(() =>
        {
            SceneManager.LoadScene("SampleScene");
        });
    }
}

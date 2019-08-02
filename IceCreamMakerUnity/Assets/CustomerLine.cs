using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using IceCreamMakerExtensions;

public class CustomerLine : MonoBehaviour
{

    public GameObject CustomerPrefab;
    public Vector3 CustomerStartPos;

    private Vector3 lastCustomerPos = Vector3.zero;
    private float customerDistApart = 2f;
    private bool dayEnded = false;

    private class CustomerData
    {
        public Transform transform;
        public Tween movSeq;
        public Vector3 targetPos;
        private Sequence jumpSeq;
        private SpriteRenderer spriteRenderer;

        private Transform speechBubble;
        private SpriteRenderer bubbleThoughts;
        public Sprite ChosenFlavour { private set; get; }


        public CustomerData(Transform t)
        {
            transform = t;
            spriteRenderer = transform.GetComponent<SpriteRenderer>();
            spriteRenderer.sprite = IceCreamResources.Instance.CustomerSprites.RandomSprite();
            ChosenFlavour = IceCreamResources.Instance.ChosenFlavours.RandomSprite();

            speechBubble = transform.Find("speech");
            bubbleThoughts = speechBubble.Find("icon").GetComponent<SpriteRenderer>();
            bubbleThoughts.sprite = ChosenFlavour;
        }

        public void ClearMovSeq()
        {
            movSeq?.Kill();
            movSeq = null;
        }
        public void DoJump()
        {
            if (jumpSeq != null)
            {
                return;
            }

            float jumpHalfTime = 0.2f + Random.Range(-0.05f, 0.05f);
            float jumpHeight = 0.8f + Random.Range(-0.1f, 0.1f);
            jumpSeq = DOTween.Sequence().Append(transform.DOLocalMoveY(jumpHeight, jumpHalfTime).SetEase(Ease.OutSine).SetRelative(true))
                .Append(transform.DOLocalMoveY(-jumpHeight, jumpHalfTime).SetEase(Ease.InSine).SetRelative(true))
                .OnComplete(() => { jumpSeq = null; DoJump(); });
        }

        public void DoMoveTo(Vector3 newPos)
        {
            movSeq = transform.DOLocalMoveX(newPos.x, 5).SetSpeedBased(true).SetEase(Ease.Linear).OnComplete(() =>
            {
                jumpSeq.OnComplete(() => jumpSeq = null);
            });
            DoJump();
        }

        public void MoveOut(int barCount)
        {
            ClearMovSeq();
            DoMoveTo(new Vector3(10, 0, 0));
            movSeq.OnComplete(() => { jumpSeq?.Kill(); Destroy(transform.gameObject); });
            bubbleThoughts.sprite = IceCreamResources.Instance.CustomerEmojis[Mathf.Clamp(3- barCount, 0, 2)];

            speechBubble.gameObject.SetActive(true);
            var currentScale = speechBubble.localScale;
            speechBubble.localScale = Vector3.zero;
            speechBubble.DOScale(currentScale, 0.3f).SetEase(Ease.InOutBounce);
        }

        public void MakeRequestOnMoveComplete()
        {
            if(movSeq != null)
            {
                movSeq.onComplete += MakeRequest;
            }
        }

        private void MakeRequest()
        {
            speechBubble.gameObject.SetActive(true);
            var currentScale = speechBubble.localScale;
            speechBubble.localScale = Vector3.zero;
            speechBubble.DOScale(currentScale, 0.3f).SetEase(Ease.InOutBounce);
        }
    };

    private List<CustomerData> customers = new List<CustomerData>();

    // Use this for initialization
    void Start()
    {
        DOTween.Sequence().AppendCallback(AddNewCustomer).AppendInterval(1).SetLoops(6).OnComplete(TryAddCustomer);
    }

    void AddNewCustomer()
    {
        if (dayEnded) { return; }

        var customer = GameObject.Instantiate(CustomerPrefab, this.transform);
        var data = new CustomerData(customer.transform);
        data.transform.localPosition = CustomerStartPos + new Vector3(Random.Range(-1, 1), 0, 0);
        data.DoMoveTo(lastCustomerPos);
        
        lastCustomerPos.x += -customerDistApart;

        customers.Add(data);

        if(customers.Count == 1)
            data.MakeRequestOnMoveComplete();
    }

    public void DayEnd()
    {
        dayEnded = true;
        //foreach(var customer in customers)
        //{
        //    customer.MoveOut(0);
        //}
        //customers.Clear();
    }

    public bool IsEmpty()
    {
        return customers.Count == 0;
    }

    public void ClearCustomer(int barCount)
    {
        if (customers.Count == 0)
            return;

        var firstCustomer = customers[0];
        firstCustomer.MoveOut(barCount);

        customers.RemoveAt(0);

        for (int i = 0; i < customers.Count; ++i)
        {
            customers[i].ClearMovSeq();
            customers[i].DoMoveTo(new Vector3(customerDistApart * -i, 0, 0));
            if(i == 0)
            {
                customers[i].MakeRequestOnMoveComplete();
            }
        }
        lastCustomerPos.x += customerDistApart;
    }

    void TryAddCustomer()
    {
        if (dayEnded) { return; }
        DOTween.Sequence().AppendInterval(Random.Range(1, 3)).AppendCallback(TryAddCustomer);

        if (customers.Count < 5)
        {
            AddNewCustomer();
        }
    }

    public Sprite GetFirstCustomerRequest()
    {
        if(customers.Count > 0)
        {
            return customers[0].ChosenFlavour;
        }
        return IceCreamResources.Instance.ChosenFlavours.RandomSprite();
    }
}

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using System.Linq;

public class GameManager : MonoBehaviour
{
    public List<IceCreamBar> BarList;

    // Start is called before the first frame update
    void Start()
    {
        DOTween.Sequence().AppendInterval(0.5f).AppendCallback(() =>
        {
            BarList.ForEach(bar => bar.TakeInNewOrder(Random.Range(2, 5)));
            this.enabled = true;
        });
        this.enabled = false;
    }

    // Update is called once per frame
    void Update()
    {
        if(Input.GetMouseButtonDown(0))
        {
            float percentage = Input.mousePosition.x / Screen.width;
            int sector = 0;
            if (percentage > 0.66f)
            {
                sector = 2;
            }
            else if(percentage > 0.33f)
            {
                sector = 1;
            }
            BarList[sector].AddScoop();
        }
        
        if(BarList.All(bar => bar.IsReadyToTakeNewOrder()))
        {
            BarList.ForEach(bar => bar.TakeInNewOrder(Random.Range(2, 7)));
        }
        else if (BarList.All(bar => bar.CanServe() || bar.IsReadyToTakeNewOrder()))
        {
            BarList.ForEach(bar => bar.DoServeAnimation());
        }
    }
}

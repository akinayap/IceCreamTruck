using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using DG.Tweening;
using IceCreamMakerExtensions;

public class ParkColorTweener : MonoBehaviour {

    public Color DayColor;
    public Color EveningColor;
    public Color NightColor;
    public float CurrentTargetPercent;

    private SpriteRenderer backgroundSprite;
    public float CurrentPercent = 0;
    // Use this for initialization
	void Start () {
        backgroundSprite = GetComponent<SpriteRenderer>();
    }

    //private void OnDrawGizmosSelected()
    //{
    //    backgroundSprite = GetComponent<SpriteRenderer>();
    //    CurrentPercent = Mathf.Clamp01(CurrentPercent);
    //    if (CurrentPercent <= 0.5f)
    //    {
    //        backgroundSprite.color = Color.Lerp(DayColor, EveningColor, CurrentPercent * 2);
    //    }
    //    else
    //    {
    //        backgroundSprite.color = Color.Lerp(EveningColor, NightColor, (CurrentPercent-0.5f) * 2);
    //    }
    //}


    // Update is called once per frame
    void Update () {
        CurrentPercent = Mathf.Lerp(CurrentPercent, CurrentTargetPercent, 0.05f);
        CurrentPercent = Mathf.Clamp01(CurrentPercent);
        if (CurrentPercent <= 0.5f)
        {
            backgroundSprite.color = Color.Lerp(DayColor, EveningColor, CurrentPercent * 2);
        }
        else
        {
            backgroundSprite.color = Color.Lerp(EveningColor, NightColor, (CurrentPercent - 0.5f) * 2);
        }
    }
}

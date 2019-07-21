using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class ScreenLogger : MonoBehaviour {

    TextMeshProUGUI debugText;
    List<string> textList = new List<string>();
    static ScreenLogger instance = null;

	// Use this for initialization
	void Start () {
        debugText = GetComponent<TextMeshProUGUI>();
        instance = this;
    }
	
	// Update is called once per frame
	void Update () {
	
	}

    static public void Log(object msg)
    {
        if(instance.textList.Count > 10)
        {
            instance.textList.Insert(0, msg == null ? "Null" : msg.ToString());
            instance.textList.RemoveAt(instance.textList.Count - 1);
        }
        else
        {
            instance.textList.Add(msg == null ? "Null" : msg.ToString());
        }

        string finalStr = "";
        foreach(var s in instance.textList)
        {
            finalStr += s + "\n";
        }
        instance.debugText.text = finalStr;
        Debug.Log(msg);
    }
}

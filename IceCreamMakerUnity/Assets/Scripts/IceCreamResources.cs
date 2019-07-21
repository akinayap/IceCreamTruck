using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(fileName = "icr", menuName = "ScriptableObjects/IceCreamResources", order = 1)]
public class IceCreamResources : ScriptableObject
{
    public GameObject CreamPrefab;
    public GameObject DottedPrefab;

    public List<Sprite> IceCreamFlavours;
    public List<string> IceCreamFlavourNames;
    public Texture DottedLineIceCream;

    public GameObject TapSparkPrefab;

    public GameManager Manager;
    public static IceCreamResources Instance
    {
        get
        {
            Load();
            return _instance;
        }
    }

    public static void Load()
    {
        if(_instance == null)
        {
            _instance = Resources.Load<IceCreamResources>("ScriptableObjects/IceCreamTexture");
        }
    }

    private static IceCreamResources _instance = null;
}

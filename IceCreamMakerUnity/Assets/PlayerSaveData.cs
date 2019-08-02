using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerSaveData{

    public static PlayerSaveData Instance => getInstance();

    private static PlayerSaveData instance;

    private static PlayerSaveData getInstance()
    {
        if(instance == null)
        {
            instance = new PlayerSaveData();
        }
        return instance;
    }

    public Dictionary<string, int> FlavourCount;

    private PlayerSaveData()
    {
        FlavourCount = new Dictionary<string, int>();
        FlavourCount["chocolate"] = int.Parse(PlayerPrefs.GetString("count_chocolate", "0"));
        FlavourCount["strawberry"] = int.Parse(PlayerPrefs.GetString("count_strawberry", "0"));
        FlavourCount["corn"] = int.Parse(PlayerPrefs.GetString("count_corn", "0"));
        FlavourCount["mint"] = int.Parse(PlayerPrefs.GetString("count_mint", "0"));
        FlavourCount["vanilla"] = int.Parse(PlayerPrefs.GetString("count_vanilla", "0"));
    }

    public void SaveAllData()
    {
        foreach(var pair in FlavourCount)
        {
            PlayerPrefs.SetInt("count_" + pair.Key, pair.Value);
        }
        PlayerPrefs.Save();
    }

}

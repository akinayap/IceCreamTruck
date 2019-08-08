using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Threading;
using System.Linq;

[System.Serializable]
public class PlayerSaveData
{
    [System.Serializable]
    public class FlavourData
    {
        public string name;
        public int own_count;
        public int serve_count;

        public FlavourData(string flavourName, int ownCount, int serveCount)
        {
            name = flavourName;
            own_count = ownCount;
            serve_count = serveCount;
        }
    };

    [System.Serializable]
    public class Person
    {
        public string name;
        public int visitCount;
        public bool hasUnlockedFavourite;

        public Person(string v1, int v2, bool v3)
        {
            name = v1;
            visitCount = v2;
            hasUnlockedFavourite = v3;
        }
    };
    
    public List<FlavourData> flavours;
    public List<Person> served_customers;

    public PlayerSaveData(PlayerSaveData rhs)
    {
        flavours = new List<FlavourData>(rhs.flavours);
        served_customers = new List<Person>(rhs.served_customers);
    }

    public PlayerSaveData()
    {
        flavours = new List<FlavourData>();
        served_customers = new List<Person>();
    }
}
public class PlayerSaveDataManager
{
    public static PlayerSaveDataManager Instance => GetInstance();

    private static PlayerSaveDataManager instance;

    public static PlayerSaveDataManager GetInstance()
    {
        if (instance == null)
        {
            instance = new PlayerSaveDataManager();
        }
        return instance;
    }

    public PlayerSaveData SaveData { get; set; }
    private volatile int isSaving = 0;
    private readonly string folderPath = "";
    private readonly string jsonPath = "";

    private PlayerSaveDataManager()
    {
        // Try load save data
        folderPath = Path.Combine(Application.persistentDataPath, "local");
        jsonPath = Path.Combine(folderPath, "data.json");

        // TODO: Uncomment
        // If file does not exist, create empty file
        //if (!Directory.Exists(folderPath))
        {
            Directory.CreateDirectory(folderPath);
            SaveData = new PlayerSaveData();
            SaveData.flavours.Add(new PlayerSaveData.FlavourData("chocolate", 1, 0));
            SaveData.flavours.Add(new PlayerSaveData.FlavourData("vanilla", 2, 0));
            SaveData.flavours.Add(new PlayerSaveData.FlavourData("strawberry", 1, 0));
            SaveData.flavours.Add(new PlayerSaveData.FlavourData("mint", 0, 0));
            SaveData.flavours.Add(new PlayerSaveData.FlavourData("corn", 0, 0));
            string data = JsonUtility.ToJson(SaveData, true);
            File.WriteAllText(jsonPath, data);
            return;
        }

        // If file exist, do blocking load
        string loadedJsonDataString = File.ReadAllText(jsonPath);
        SaveData = JsonUtility.FromJson<PlayerSaveData>(loadedJsonDataString);

        // Sanity check for no duplicates
        SaveData.served_customers = SaveData.served_customers.GroupBy(x => x.name).Select(y => y.First()).ToList();
        SaveData.flavours = SaveData.flavours.GroupBy(x => x.name).Select(y => y.First()).ToList();

        //TODO: Make sure at least 3 flavours
    }

    public void SaveAsync()
    {
        var dataCopy = new PlayerSaveData(SaveData);
        new Thread(()=> SaveDataThreaded(dataCopy)).Start();
    }

    private void SaveDataThreaded(PlayerSaveData saveData)
    {
        // Lock
        while (Interlocked.CompareExchange(ref isSaving, 1, 0) == 0);

        // Sanity check for no duplicates
        saveData.served_customers = saveData.served_customers.GroupBy(x => x.name).Select(y => y.First()).ToList();
        saveData.flavours = saveData.flavours.GroupBy(x => x.name).Select(y => y.First()).ToList();

        // Convert to json and save
        string strData = JsonUtility.ToJson(saveData, true);
        File.WriteAllText(jsonPath, strData);
        
        // Unlock
        isSaving = 0;
    }

}
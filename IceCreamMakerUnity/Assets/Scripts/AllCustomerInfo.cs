using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;

public class CustomersAndFlavours {

    public class Customer
    {
        public string name = "";
        public string favouriteFlavour = "";
        public int favouriteFlavourIndex = -1;
        public bool hasUnlockedFavourite = false;
        public int visitCount = 0;
        public Sprite sprite = null;
    };

    public class Flavour
    {
        public string name = "";
        public int index = 0;
        public int own_count = 0;
        public int served_count = 0;

        public struct Require
        {
            public int flavourIndex;
            public int flavourCount;
        };
        public List<Require> requires;
        public int coinCost;

        public Sprite sprite;
        public bool unlocked = false;
    };

    public SortedDictionary<string, Customer> allCustomers;

    public SortedDictionary<string, int> flavourNameMapping;
    public List<Flavour> allFlavours;

    private static CustomersAndFlavours _instance = null;
    public static CustomersAndFlavours Instance
    {
        get
        {
            if(_instance == null)
            {
                _instance = new CustomersAndFlavours();
                _instance.Load();
            }
            return _instance;
        }
    }

    public static CustomersAndFlavours GetInstance() => Instance;

    private void Load () {
        allCustomers = new SortedDictionary<string, Customer>();
        flavourNameMapping = new SortedDictionary<string, int>();
        allFlavours = new List<Flavour>();

        PlayerSaveDataManager.GetInstance();
        LoadCustomersStaticValues();
        LoadCustomerDynamicValues();
    }

    private void LoadCustomerDynamicValues()
    {
        var savedData = PlayerSaveDataManager.Instance.SaveData;
        foreach (var served_customer in savedData.served_customers)
        {
            Customer c;
            if(allCustomers.TryGetValue(served_customer.name, out c))
            {
                c.visitCount = served_customer.visitCount;
                c.hasUnlockedFavourite = served_customer.hasUnlockedFavourite;
                c.visitCount = served_customer.visitCount;
            }
        }

        foreach (var flavour in savedData.flavours)
        {
            var f = allFlavours[flavourNameMapping[flavour.name]];
            f.unlocked = true;
            f.own_count = flavour.own_count;
            f.served_count = flavour.serve_count;
        }
    }

    private void LoadCustomersStaticValues()
    {
        var allData = Resources.LoadAll<PeopleData>("");

        //One pass to load ice cream flavours
        foreach (var bundleData in allData)
        {
            foreach (var flavour in bundleData.FlavourList)
            {
                var f = new Flavour();
                f.name = flavour.name;
                f.sprite = flavour.sprite;
                f.index = allFlavours.Count;
                f.own_count = f.served_count = 0;
                f.coinCost = flavour.merge_money;

                flavourNameMapping[f.name] = f.index;
                allFlavours.Add(f);
            }
        }
        
        //Second pass to load customer and flavour dependency
        foreach (var bundleData in allData)
        {
            foreach (var customer in bundleData.CustomerList)
            {
                var c = new Customer();
                c.name = customer.name;
                c.favouriteFlavour = customer.favouriteFlavour;

                c.favouriteFlavourIndex = flavourNameMapping[customer.favouriteFlavour];
                c.sprite = customer.sprite;
                allCustomers.Add(c.name, c);
            }

            foreach(var flavour in bundleData.FlavourList)
            {
                var f = allFlavours[flavourNameMapping[flavour.name]];
                f.requires = new List<Flavour.Require>();
                foreach(var dependency in flavour.requires)
                {
                    var requirement = new Flavour.Require();
                    requirement.flavourCount = dependency.require_count;
                    requirement.flavourIndex = flavourNameMapping[dependency.name];
                    f.requires.Add(requirement);
                }
            }
        }
    }
}

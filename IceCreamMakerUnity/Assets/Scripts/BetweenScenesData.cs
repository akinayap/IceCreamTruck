using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BetweenScenesData {

    static BetweenScenesData _instance;
    public static BetweenScenesData Instance { get
        {
            if (_instance == null)
            {
                _instance = new BetweenScenesData();
            }
            return _instance;
        } }

    public List<CustomersAndFlavours.Flavour> FlavoursToUse = new List<CustomersAndFlavours.Flavour>();
    
}

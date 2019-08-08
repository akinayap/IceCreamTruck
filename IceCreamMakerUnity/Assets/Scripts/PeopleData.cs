using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(fileName = "personstaticdata", menuName = "ScriptableObjects/PersonStaticData", order = 1)]
public class PeopleData : ScriptableObject {

    [System.Serializable]
    public class Person
    {
        public string name;
        public Sprite sprite;
        public string favouriteFlavour;
    };

    [System.Serializable]
    public class Flavour
    {
        public string name;
        public Sprite sprite;

        [System.Serializable]
        public class FlavourRequires
        {
            public string name;
            public int require_count;
        };

        public List<FlavourRequires> requires;
        public int merge_money;
        public int merge_diamond;
    };

    public List<Person> CustomerList;

    public List<Flavour> FlavourList;
}

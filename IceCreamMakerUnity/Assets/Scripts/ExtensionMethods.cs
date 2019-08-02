using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace IceCreamMakerExtensions
{
    public static class ExtensionMethods 
    {
        public static Color ChangeA(this Color c, float a)
        {
            return new Color(c.r, c.g, c.b, a);
        }
        public static Vector3 ChangeY(this Vector3 vec, float y)
        {
            return new Vector3(vec.x, y, vec.z);
        }

        public static T RandomElem<T>(this List<T> list)
        {
            return list[Random.Range(0, list.Count)];
        }

        public static Sprite RandomSprite(this List<Sprite> spriteList)
        {
            return spriteList[Random.Range(0, spriteList.Count)];
        }

    }
}
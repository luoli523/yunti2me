package luoli523.guava.bimap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class BimapExample {

  public void bimapTest() {
    BiMap<String, String> capitalCountryBiMap = HashBiMap.create();
    capitalCountryBiMap.put("Mumbai", "India");
    capitalCountryBiMap.put("Washington, D.C.", "USA");
    capitalCountryBiMap.put("Moscow", "Russia");
    capitalCountryBiMap.forcePut("New Delhi", "India");
  }
}

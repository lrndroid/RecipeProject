package com.poorni.project.cooknstore.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Harini on 3/15/16.
 */
   public class ShoppingListDataSet implements Serializable
    {
        public List<ShoppingListItems> gShoppingListItems = Collections.EMPTY_LIST;
        transient private static ShoppingListDataSet shoppingLists;
        private ShoppingListDataSet()
        {

        }

        public  static ShoppingListDataSet getInstance()
        {
            if(shoppingLists == null){
                shoppingLists = new ShoppingListDataSet();
            }
            return shoppingLists;
        }
        public void addItem(List<String> items, String shoppingListName)
        {
            if(gShoppingListItems.isEmpty())
                gShoppingListItems = new ArrayList<>();
            gShoppingListItems.add(new ShoppingListItems(items,shoppingListName));
        }


       public class ShoppingListItems implements Serializable
        {
              public List<String> items;
             public String shoppingListName;
            public ShoppingListItems(List<String> items,String shoppingListName)
            {
                this.items = items;
                this.shoppingListName = shoppingListName;

            }
        }
    }


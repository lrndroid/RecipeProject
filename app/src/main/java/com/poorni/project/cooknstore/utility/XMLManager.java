package com.poorni.project.cooknstore.utility;

import android.content.Context;
import android.provider.DocumentsContract;
import android.util.Xml;

import org.w3c.dom.Element;

import com.poorni.project.cooknstore.RecipeStorage;
import com.poorni.project.cooknstore.data.IngredientDataSet;
import com.poorni.project.cooknstore.data.ShoppingListDataSet;


import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Harini on 3/22/16.
 */
public class XMLManager {
    public Context mContext;
    public File takeBackup(List<RecipeStorage> list)  {
        File backupFile =null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("Recipes");
            doc.appendChild(root);

            for (RecipeStorage storage : list) {
                Element recipe = doc.createElement("recipe");
                root.appendChild(recipe);

                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(storage.getName()));
                recipe.appendChild(name);
/*                Element category = doc.createElement("category");
                category.appendChild(doc.createTextNode(storage.getCategory()));
                recipe.appendChild(category);*/
                Element time = doc.createElement("time");
                time.appendChild(doc.createTextNode(storage.getTotalTime()));
                recipe.appendChild(time);
                Element serves = doc.createElement("serves");
                serves.appendChild(doc.createTextNode(storage.getServes()));
                recipe.appendChild(serves);
                Element url = doc.createElement("url");
                url.appendChild(doc.createTextNode(storage.getPictureUrl()));
                recipe.appendChild(url);
                Element directions = doc.createElement("directions");
                directions.appendChild(doc.createTextNode(storage.getDirections()));
                recipe.appendChild(directions);
                Element ingredients = doc.createElement("ingredients");
                recipe.appendChild(ingredients);
                for (IngredientDataSet ds : storage.getIngredientList()) {
                    Element ingredient = doc.createElement("ingredient");
                    ingredients.appendChild(ingredient);
                    Element ingredientname = doc.createElement("ingredientname");
                    ingredientname.appendChild(doc.createTextNode(ds.getIngredientName()));
                    ingredient.appendChild(ingredientname);
                    Element quantity = doc.createElement("quantity");
                    quantity.appendChild(doc.createTextNode(ds.getIngredientQuantity()));
                    ingredient.appendChild(quantity);
                    Element unit = doc.createElement("unit");
                    unit.appendChild(doc.createTextNode(ds.getIngredientUnit()));
                    ingredient.appendChild(unit);
                    Element position = doc.createElement("position");
                    position.appendChild(doc.createTextNode(String.valueOf(ds.getUnitPosition())));
                    ingredient.appendChild(position);
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            try {

/*                FileOutputStream fos = mContext.openFileOutput("backuprecipe.xml", mContext.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(source);*/
                backupFile = FileManager.getOutputMediaFile(2,mContext);
                FileWriter fos = new FileWriter(backupFile);
                StreamResult result = new StreamResult(fos);
                transformer.transform(source, result);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }catch (TransformerException ex) {
            System.out.println("Error outputting document");

        } catch (ParserConfigurationException ex) {
            System.out.println("Error building document");
        }
        return backupFile;
    }
    private static final String ns = null;

    public  ArrayList  parseXml(InputStream stream) throws IOException {

        ArrayList<RecipeStorage> importList = null;
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);
            parser.nextTag();
            importList = readRecipes(parser);
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            stream.close();
        }
        return importList;
    }
    private ArrayList readRecipes(XmlPullParser parser)throws XmlPullParserException, IOException {
        ArrayList<RecipeStorage> importList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG,ns,"Recipes");
        while (parser.next()!= XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if(name.equals("recipe")){
                importList.add(readData(parser));
            }else {
                skip(parser);
            }
        }
    return importList;
    }

    private RecipeStorage readData(XmlPullParser parser) throws XmlPullParserException, IOException{
        ShoppingListDataSet.ShoppingListItems shoppingListItems = null;
        List<IngredientDataSet> ingredientList = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "recipe");
        String name = null;
        String time = null;
        String serves = null;
        String url = null;
        String directions = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals("name")) {
                name = readText(parser);
            } else if (tag.equals("time")) {
                time = readText(parser);
            } else if (tag.equals("serves")) {
                serves = readText(parser);
            } else if(tag.equals("url")){
                url = readText(parser);
            }else if(tag.equals("directions")){
                directions = readText(parser);
            }else if(tag.equals("ingredients"))
                while (parser.next()!= XmlPullParser.END_TAG){
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
               String innertag = parser.getName();
                if(innertag.trim().equals("ingredient")){
                    ingredientList.add(readIngredients(parser));
                }else {
                    skip(parser);
                }
            }

        }RecipeStorage storage = new RecipeStorage(-1,name,"",serves,time,url,directions,ingredientList);
        return storage;
    }

    private IngredientDataSet readIngredients(XmlPullParser parser)throws XmlPullParserException,IOException{
        parser.require(XmlPullParser.START_TAG, ns, "ingredient");
         String ingredientName= null;
         String quantity=null;
         String unit=null;
         int position=-1;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals("ingredientname")) {
                ingredientName = readText(parser);
            } else if (tag.equals("quantity")) {
                quantity = readText(parser);
            } else if (tag.equals("unit")) {
                unit = readText(parser);
            } else if(tag.equals("position")) {
                position = Integer.parseInt(readText(parser));
            }
        }
        IngredientDataSet ds = new IngredientDataSet(ingredientName,quantity,unit,position);
        return ds;
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}

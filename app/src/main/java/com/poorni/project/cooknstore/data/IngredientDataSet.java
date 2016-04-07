package com.poorni.project.cooknstore.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Harini on 3/8/16.
 */
public class IngredientDataSet implements Parcelable {
    public String IngredientName;
    public String IngredientQuantity;
    public String IngredientUnit;
    long Id;

    public long getId() {return Id;}

    public void setId(long id){this.Id = id;}

    public int getUnitPosition() {
        return UnitPosition;
    }

    public void setUnitPosition(int unitPosition) {
        UnitPosition = unitPosition;
    }

    int UnitPosition;
    public String getIngredientUnit() {return IngredientUnit;}

    public void setIngredientUnit(String ingredientUnit) {IngredientUnit = ingredientUnit;}

    public String getIngredientQuantity() {return IngredientQuantity;}

    public void setIngredientQuantity(String ingredientQuantity) {IngredientQuantity = ingredientQuantity;}

    public String getIngredientName() {return IngredientName;}

    public void setIngredientName(String ingredientName) {IngredientName = ingredientName;}

    public IngredientDataSet(String ingredientName, String ingredientQuantity, String ingredientUnit, int position)
    {
        this.IngredientName = ingredientName;
        this.IngredientQuantity = ingredientQuantity;
        this.IngredientUnit = ingredientUnit;
        this.UnitPosition = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    private IngredientDataSet(Parcel in) {

        this.IngredientName = in.readString();
        this.IngredientQuantity = in.readString();
        this.IngredientUnit = in.readString();
        this.UnitPosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IngredientName);
        dest.writeString(IngredientQuantity);
        dest.writeString(IngredientUnit);
        dest.writeInt(UnitPosition);
    }

    public static final Parcelable.Creator<IngredientDataSet> CREATOR = new Parcelable.Creator<IngredientDataSet>() {
        public IngredientDataSet createFromParcel(Parcel in) {
            return new IngredientDataSet(in);
        }

        @Override
        public IngredientDataSet[] newArray(int size) {
            return new IngredientDataSet[size];
        }
    };
}

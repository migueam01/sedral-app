package com.mam.alicamp.servicios;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputLayout;
import com.mam.alicamp.db.entidades.Pozo;

import java.util.List;
import java.util.Objects;

public class Validaciones {

    public static boolean validarCamposVacios(EditText editText, String mensajeError) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(mensajeError);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    public static boolean emptyTextInputLayout(TextInputLayout textInputLayout) {
        if (Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().isEmpty()) {
            textInputLayout.getEditText().setError("Campo requerido");
            textInputLayout.getEditText().requestFocus();
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static int isCheckedRGroup(RadioGroup radioGroup) {
        return radioGroup.getCheckedRadioButtonId();
    }

    public static boolean checkHeights(String flujo, double heightBase, double heightPozo) {
        boolean check = false;
        switch (flujo) {
            case "Entra":
                if (heightBase <= heightPozo) {
                    check = true;
                }
                break;
            case "Sale":
            case "Inicio":
                double heightDifference = heightBase - heightPozo;
                heightDifference = Utilitarios.redondearDecimales(heightDifference, 2);
                if (heightDifference >= 0.01 && heightDifference <= 0.02) {
                    check = true;
                }
                break;
            default:
                break;
        }
        return check;
    }

    public static boolean isRepeated(String nombre, List<Pozo> listSearch, TextInputLayout textInputLayout) {
        boolean repeated = true;
        textInputLayout.setError(null);
        for (Pozo pozo : listSearch) {
            if (pozo.getNombre().equalsIgnoreCase(nombre)) {
                repeated = false;
                Objects.requireNonNull(textInputLayout.getEditText()).setError("El pozo ya existe");
                textInputLayout.getEditText().requestFocus();
                break;
            }
        }
        return repeated;
    }
}
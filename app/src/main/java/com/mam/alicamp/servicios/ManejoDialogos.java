package com.mam.alicamp.servicios;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static com.mam.alicamp.constantes.Constantes.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mam.alicamp.R;

public class ManejoDialogos extends DialogFragment {

    private View view;

    private String titleDialog;
    private String oldName;

    public ManejoDialogos() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearEditarDialog();
    }

    public interface IDialogHandling {
        void onDialogPositiveClick(DialogFragment dialog, EditText editText, String typeFolder);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    private IDialogHandling listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IDialogHandling) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement NoticeDialogListener");
        }
    }

    private AlertDialog crearEditarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_crear, null);
        addIconToolbar();
        addTitle();
        builder.setView(view);
        final EditText editTextName = view.findViewById(R.id.editTextNameDialog);
        if (oldName != null) {
            editTextName.setText(oldName);
        }
        builder.setPositiveButton("OK", (dialog, which) -> listener
                        .onDialogPositiveClick(ManejoDialogos.this, editTextName, titleDialog))
                .setNegativeButton("Cancel", (dialog, which) -> listener
                        .onDialogNegativeClick(ManejoDialogos.this));
        return builder.create();
    }

    private void addTitle() {
        TextView textViewTitle = view.findViewById(R.id.textViewTitleDialog);
        textViewTitle.setText(titleDialog);
    }

    private void addIconToolbar() {
        ImageView imgViewIcon = view.findViewById(R.id.imgViewIcon);
        switch (titleDialog) {
            case GADM:
                imgViewIcon.setImageResource(R.drawable.ic_gadm);
                break;
            case PROYECTO:
                imgViewIcon.setImageResource(R.drawable.briefcase_plus);
                break;
            case SECTOR:
                imgViewIcon.setImageResource(R.drawable.ic_add_sector);
                break;
            case RESPONSABLE:
                imgViewIcon.setImageResource(R.drawable.account_plus);
                break;
            case DESCARGA:
                imgViewIcon.setImageResource(R.drawable.waves);
                break;
        }
    }

    public void crearDialogo(String message, Context contextDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(contextDialog);
        builder.setMessage(message)
                .setTitle(R.string.title_dialog_warning)
                .setPositiveButton("ACEPTAR",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String getTitleDialog() {
        return titleDialog;
    }

    public void setTitleDialog(String titleDialog) {
        this.titleDialog = titleDialog;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
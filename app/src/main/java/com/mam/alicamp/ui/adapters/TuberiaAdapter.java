package com.mam.alicamp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mam.alicamp.R;
import com.mam.alicamp.db.entidades.Tuberia;

import java.util.List;

public class TuberiaAdapter extends RecyclerView.Adapter<TuberiaAdapter.TuberiaViewHolder> {

    private List<Tuberia> listaTuberias;
    private final OnItemClickListener listener;

    public TuberiaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TuberiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tuberia_list_item, parent, false);
        return new TuberiaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TuberiaViewHolder holder, int position) {
        Tuberia tuberia = listaTuberias.get(position);
        holder.bind(tuberia, listener);
    }

    @Override
    public int getItemCount() {
        return listaTuberias == null ? 0 : listaTuberias.size();
    }

    public void setTuberias(List<Tuberia> tuberias) {
        this.listaTuberias = tuberias;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onItemClick(Tuberia tuberia);

        void onEliminarClick(Tuberia tuberia);
    }

    public static class TuberiaViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtOrientacion;
        private final TextView txtPozoFin;
        private final TextView txtFlujo;
        private final TextView txtMaterialDiametro;
        private final ImageView imgBtnEliminar;

        public TuberiaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtOrientacion = itemView.findViewById(R.id.txtOrientacion);
            this.txtPozoFin = itemView.findViewById(R.id.txtPozoFin);
            this.txtFlujo = itemView.findViewById(R.id.txtFlujo);
            this.txtMaterialDiametro = itemView.findViewById(R.id.txtMaterialDiametro);
            this.imgBtnEliminar = itemView.findViewById(R.id.imgBtnEliminarTuberia);
        }

        public void bind(Tuberia tuberia, final OnItemClickListener listener) {
            String material = tuberia.getMaterial();
            String diametro = String.valueOf(tuberia.getDiametro());
            String orientacion = tuberia.getOrientacion();
            this.txtOrientacion.setText(devolverOrientacion(orientacion));
            this.txtPozoFin.setText(String.format("Pozo fin: %s", tuberia.getIdPozoFin()));
            this.txtFlujo.setText(String.format("Flujo: %s", tuberia.getFlujo()));
            this.txtMaterialDiametro.setText(String.format("%s; Ø= %smm", material, diametro));
            itemView.setOnClickListener(v -> listener.onItemClick(tuberia));
            imgBtnEliminar.setOnClickListener(v -> listener.onEliminarClick(tuberia));
        }

        private String devolverOrientacion(String orientacion) {
            String simboloOrientacion;
            switch (orientacion) {
                case "Norte":
                    simboloOrientacion = "N";
                    break;
                case "Sur":
                    simboloOrientacion = "S";
                    break;
                case "Este":
                    simboloOrientacion = "E";
                    break;
                case "Oeste":
                    simboloOrientacion = "O";
                    break;
                default:
                    simboloOrientacion = "";
                    break;
            }
            return simboloOrientacion;
        }
    }
}
package com.mam.alicamp.ui.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.mam.alicamp.R;
import com.mam.alicamp.db.entidades.Pozo;

import java.io.File;
import java.util.List;

import com.bumptech.glide.Glide;

public class PozoAdapter extends RecyclerView.Adapter<PozoAdapter.PozoViewHolder> {

    private List<Pozo> listaPozos;
    private final OnItemClickListener listener;

    public PozoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PozoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pozo_list_item, parent, false);
        return new PozoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PozoViewHolder holder, int position) {
        Pozo pozo = listaPozos.get(position);
        holder.bind(pozo, listener);
    }

    @Override
    public int getItemCount() {
        return listaPozos == null ? 0 : listaPozos.size();
    }

    public void setPozos(List<Pozo> pozos) {
        this.listaPozos = pozos;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Pozo pozo);

        void onItemLongClick(View view, Pozo pozo);

        void onEliminarClick(Pozo pozo);
    }

    public static class PozoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgViewFoto;
        private final TextView txtNombrePozo;
        private final TextView txtAltura;
        private final TextView txtTapado;
        private final ShapeableImageView shapeEstado;
        private final ImageView imgBtnEliminar;

        public PozoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imgViewFoto = itemView.findViewById(R.id.imgViewFoto);
            this.imgBtnEliminar = itemView.findViewById(R.id.imgBtnEliminarPozo);
            this.txtNombrePozo = itemView.findViewById(R.id.txtNombrePozo);
            this.txtAltura = itemView.findViewById(R.id.txtAltura);
            this.txtTapado = itemView.findViewById(R.id.txtTapado);
            this.shapeEstado = itemView.findViewById(R.id.shapeEstado);
        }

        public void bind(Pozo pozo, final OnItemClickListener listener) {
            String pathFoto = pozo.getPathMedia() + pozo.getNombre() + "-U.jpg";
            String pozoTapado = pozo.getTapado();
            File file = new File(pathFoto);
            this.txtNombrePozo.setText(pozo.getNombre());
            this.txtAltura.setText(String.format("Altura = %sm", pozo.getAltura()));
            this.txtTapado.setText(String.format("Tapado: %s", pozoTapado));
            int colorEstado;
            try {
                colorEstado = cambiarColorEstado(pozo.getEstado());
            } catch (NullPointerException np) {
                colorEstado = cambiarColorEstado("Default");
            }
            Drawable estadoDrawable = ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.circulo_estado);
            if (estadoDrawable != null) {
                estadoDrawable.setColorFilter(
                        ContextCompat.getColor(itemView.getContext(), colorEstado),
                        PorterDuff.Mode.SRC_IN
                );
                this.shapeEstado.setImageDrawable(estadoDrawable);
            }
            if (file.exists() && file.canRead()) {
                Glide.with(imgViewFoto.getContext())
                        .load(pathFoto)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.folder_edit)
                        .error(R.drawable.sin_foto_pozo)
                        .into(imgViewFoto);
            } else {
                Glide.with(imgViewFoto.getContext())
                        .load(R.drawable.sin_foto_pozo)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgViewFoto);
            }
            itemView.setOnClickListener(v -> listener.onItemClick(pozo));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(v, pozo);
                return true;
            });
            imgBtnEliminar.setOnClickListener(v -> listener.onEliminarClick(pozo));
        }

        private int cambiarColorEstado(String estadoPozo) {
            switch (estadoPozo.toLowerCase()) {
                case "bueno":
                    return R.color.estado_bueno;
                case "regular":
                    return R.color.estado_regular;
                case "malo":
                    return R.color.estado_malo;
                default:
                    return R.color.estado_por_defecto;
            }
        }
    }
}

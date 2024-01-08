package org.example.eoliiri.proyecto_3a;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class informacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);

        ViewPager2 viewPager1 = findViewById(R.id.viewPager1);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        ViewPager2 viewPager3 = findViewById(R.id.viewPager3);
        ViewPager2 viewPager4 = findViewById(R.id.viewPager4);
        ViewPager2 viewPager5 = findViewById(R.id.viewPager5);

        MyFragmentAdapter adapter1 = new MyFragmentAdapter(getSupportFragmentManager(), getLifecycle(), createFragments(1));
        MyFragmentAdapter adapter2 = new MyFragmentAdapter(getSupportFragmentManager(), getLifecycle(), createFragments(2));
        MyFragmentAdapter adapter3 = new MyFragmentAdapter(getSupportFragmentManager(), getLifecycle(), createFragments(3));
        MyFragmentAdapter adapter4 = new MyFragmentAdapter(getSupportFragmentManager(), getLifecycle(), createFragments(4));
        MyFragmentAdapter adapter5 = new MyFragmentAdapter(getSupportFragmentManager(), getLifecycle(), createFragments(5));

        viewPager1.setAdapter(adapter1);
        viewPager2.setAdapter(adapter2);
        viewPager3.setAdapter(adapter3);
        viewPager4.setAdapter(adapter4);
        viewPager5.setAdapter(adapter5);
    }

    private List<Fragment> createFragments(int viewPagerIndex) {
        List<Fragment> fragments = new ArrayList<>();
        switch (viewPagerIndex) {
            case 1:
                fragments.add(new TextFragment("  PM2.5 y PM10 pueden penetrar en las vías respiratorias y " +
                        "causar enfermedades respiratorias, enfermedades cardíacas, cáncer y muertes."));
                fragments.add(new TextFragment("  Las partículas son pequeñas partículas que se originan en la industria, " +
                        "los gases de escape de vehículos, sitios de construcción, actividades agrícolas y fuentes naturales, " +
                        "como tormentas de arena."));
                fragments.add(new TextFragment("  La Organización Mundial de la Salud (OMS) recomienda que la concentración anual promedio de PM2.5 " +
                        "no supere los 10 microgramos por metro cúbico."));
                fragments.add(new TextFragment("  Para respirar un aire más limpio, se sugiere el uso de mascarillas, evitar actividades al aire libre, " +
                        "reducir la conducción de vehículos y utilizar purificadores de aire, entre otras medidas."));
                break;
            case 2:
                fragments.add(new TextFragment("  El SO2 puede desencadenar problemas en el sistema respiratorio, especialmente en pacientes con asma y niños."));
                fragments.add(new TextFragment("  El SO2 proviene principalmente de la quema de carbón y petróleo, así como de procesos industriales."));
                fragments.add(new TextFragment("  Las normas de calidad del aire generalmente establecen que la concentración anual promedio de SO2 no debe superar los 20 microgramos por metro cúbico."));
                fragments.add(new TextFragment("  Utilizar fuentes de energía limpia, mejorar el control de emisiones industriales y reducir las actividades al aire libre."));
                break;
            case 3:
                fragments.add(new TextFragment("  La exposición al dióxido de carbono (CO2) puede provocar dolores de cabeza, náuseas, pérdida de conocimiento e incluso resultar fatal."));
                fragments.add(new TextFragment("  El CO2 es un gas incoloro e inodoro que se genera durante la combustión de materiales orgánicos."));
                fragments.add(new TextFragment(" Es crucial mantener la concentración de CO2 en niveles seguros, generalmente no debiendo superar los 9 miligramos por metro cúbico."));
                fragments.add(new TextFragment("  Para garantizar la seguridad, es importante asegurar el correcto funcionamiento de dispositivos como calentadores de gas y estufas de carbón, así como mantener una ventilación adecuada."));
                break;
            case 4:
                fragments.add(new TextFragment("  El NO2 puede desencadenar enfermedades respiratorias, empeorar los síntomas del asma y afectar la función pulmonar."));
                fragments.add(new TextFragment("  El NO2 proviene principalmente de los gases de escape del tráfico y las emisiones industriales."));
                fragments.add(new TextFragment("  Por lo general, la concentración de NO2 debe mantenerse por debajo de los 40 microgramos por metro cúbico como promedio anual."));
                fragments.add(new TextFragment("  Reducir el uso de automóviles, utilizar el transporte público y fomentar modos de transporte no motorizados."));
                break;
            case 5:
                fragments.add(new TextFragment("  La contaminación por ozono puede causar dificultad para respirar, irritación ocular y empeorar el asma, entre otros efectos."));
                fragments.add(new TextFragment("  La contaminación por ozono en superficie se origina principalmente a partir de reacciones atmosféricas de compuestos orgánicos volátiles (COVs) y óxidos de nitrógeno (NOx)."));
                fragments.add(new TextFragment("  La concentración de ozono debe mantenerse por debajo de los 100 microgramos por metro cúbico como promedio anual."));
                fragments.add(new TextFragment("  Evitar áreas de alta contaminación, reducir el uso de productos químicos y apoyar medios de transporte ecológicos."));
                break;
        }
        return fragments;
    }
}


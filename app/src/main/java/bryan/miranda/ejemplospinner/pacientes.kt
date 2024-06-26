package bryan.miranda.ejemplospinner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassDoctores
import kotlin.coroutines.coroutineContext

class pacientes : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val root = inflater.inflate(R.layout.fragment_pacientes, container, false)
        //spinner
        val spDoctores = root.findViewById<Spinner>(R.id.spDoctores)
        val txtFechaNacimiento = root.findViewById<EditText>(R.id.txtFechaNacimiento)

        //creamos la funcion que haga un select
        fun obtenerDoctores(): List<dataClassDoctores>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbDoctores")!!

            val listaDoctores = mutableListOf<dataClassDoctores>()

            while(resultSet.next()){
                val uuid = resultSet.getString("DoctorUUID")
                val nombreDoctor = resultSet.getString("nombreDoctor")
                val Especialidad = resultSet.getString("Especialidad")
                val Telefono = resultSet.getString("Telefono")

                val unDoctorCompleto = dataClassDoctores(uuid, nombreDoctor, Especialidad, Telefono)
                listaDoctores.add(unDoctorCompleto)
            }
            return listaDoctores
        }

        CoroutineScope(Dispatchers.IO).launch {
            //obtener el listado de datos que quiero mostrar
            val listaDoctores = obtenerDoctores()
            //esto busca lo que se quiere mostrar
            val nombreDoctores = listaDoctores.map { it.nombreDoctor }

            withContext(Dispatchers.Main){
                val miAdaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombreDoctores)
                spDoctores.adapter = miAdaptador

            }
        }


//Mostrar el calendario al hacer click en el EditText txtFechaNacimientoPaciente
        txtFechaNacimiento.setOnClickListener {
            val calendario = CalendarView.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada =
                        "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaNacimiento.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )
            datePickerDialog.show()
        }


        return root

    }
}
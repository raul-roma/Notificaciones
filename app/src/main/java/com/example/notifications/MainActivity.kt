package com.example.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notifications.databinding.ActivityMainBinding
const val CHANNEL_OTHERS = "others"


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    // se setea para las versiones menores de 33 para pedir permisos y pedirlo
    //y si tienen se setea con la variable
    var hasNotificationPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationChannel()
        }

        //Sirve para pedir permisos a posteriores a versiones 22
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }

        binding.apply{
            btnNotify.setOnClickListener {
                simpleNotification()
            }
            btnActionNotify.setOnClickListener{
                touchNotification()
            }

            btnNotifyWithBtn.setOnClickListener{
                buttonNotification()
            }
        }
    }

    //preguntar si el permiso de notificaciones lo tiene
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
           //seteamos valos de hass
            hasNotificationPermissionGranted = isGranted
            //en caso de no tener enviar permisos
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            20
                        )
                    }
                }
            }
        }


    @RequiresApi(Build.VERSION_CODES.O)
    //iniciamos notificacion
     private fun setNotificationChannel(){
         val name = getString(R.string.channel_courses)
         val descriptionText = getString(R.string.courses_description)
         val importance = NotificationManager.IMPORTANCE_DEFAULT
        //Creacion de canal
        val channel = NotificationChannel(
            CHANNEL_OTHERS,
            name,
            importance
            ).apply {
              description =  descriptionText
        }

       // APARTE DE CREAR PERMITE ENVIAR NOTIFICACIONES
        val notificationManager:NotificationManager
        //castins as para recuperar NotificationManager
        = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //creacion de canal
        notificationManager.createNotificationChannel(channel)
     }

    @SuppressLint("MissingPermission")
    private fun simpleNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.triforce)
            .setColor(getColor(R.color.triforce))
            .setContentTitle(getString(R.string.simple_title))
            .setContentText(getString(R.string.simple_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).apply {
            notify(20, notification)
        }
    }

    //hacer tap a notificacion y que nos lleve a otra pantalla nos vamos a proyecto y notificationes
    @SuppressLint("MissingPermission")
    private fun touchNotification() {
        //para navegar a la nueva pantalla si queremos viajar directo ponemos el nombre directo
        //val intent = Intent(this, NewWindonActivity::class.java)

        val intent = Intent(this, NewWindonActivity::class.java).apply {
            //si no tenemos una actividad nueva crea una nueva si no la limpia
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)


        val notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.bedu_icon)
            .setContentTitle(getString(R.string.action_title))
            .setContentText(getString(R.string.action_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            //cuando demos click a la notificacion desaparezca automaticamente
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).apply {
            notify(20, notification)
        }
    }



    @SuppressLint("MissingPermission")
    private fun buttonNotification() {
        val intent = Intent(this, NotificatioReciver::class.java).apply {
           action = NotificatioReciver.ACTION_RECEIVED
        }

        val pendingIntent: PendingIntent = PendingIntent
            .getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_OTHERS)
            .setSmallIcon(R.drawable.bedu_icon)
            .setContentTitle(getString(R.string.button_title))
            .setContentText(getString(R.string.button_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.bedu_icon, getString(R.string.button_text),pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).apply {
            notify(20, notification)
        }
    }

}
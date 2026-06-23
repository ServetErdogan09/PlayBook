package com.serveterdogan.playbook.navigation

import com.serveterdogan.playbook.R

sealed class Routes( val route : String , val  icon : Int  , val  title : String){

    object PlayList : Routes("PlayList", R.drawable.playbook, "Playbook")
    
    object Editor : Routes("editor?playbookId={playbookId}", R.drawable.editor, "Editör") {
        fun createRoute(playbookId: String) = "editor?playbookId=$playbookId"
    }
    
    object AnimationViewer : Routes("animationViewer?playbookId={playbookId}", R.drawable.playbottom, "İzle") {
        fun createRoute(playbookId: String) = "animationViewer?playbookId=$playbookId"
    }
}
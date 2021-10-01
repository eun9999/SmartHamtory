from django.urls import path

from . import views

urlpatterns = [
    path('', views.mainpage, name='mainpage'),
    path('<int:num>/', views.mainpage_menuselect, name='mainpage_menuselect'),
    path('signup/', views.user_sign, name='user_sign'),
    path('delete/', views.user_del, name='user_del'),
    path('onesearch/', views.one_search_user_id, name='user_one_search'),
    path('allsearch/', views.all_search_user_id, name='user_all_search'),
    path('datarecive/', views.menu1_receive_data, name='menu1_data_receive'),
    path('errorreceive/', views.receive_error_data, name='receive_error_data'),
    path('onlyloginvalue/', views.only_login_value, name='only_login_value'),
    path('trilateration_rssi/', views.trilateration_rssi, name='trilateration_rssi'),
    path('setuserlocation/', views.change_user_location, name='change_user_location'),
]

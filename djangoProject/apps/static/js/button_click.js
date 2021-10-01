$(document).ready(function () {
    var timer;
    $("#click_menu_1").click(function() {
        $("#click_menu_1").attr('class','menu_container_selected');
        $("#click_menu_2").attr('class','menu_container');
        $("#click_menu_3").attr('class','menu_container');
        $("#click_menu_4").attr('class','menu_container');
        $("#title_select_menu").html('<a>설비 조회</a>');
        Ajaxfun.call_site(1);
        clearInterval(timer);
        timer = setInterval(Ajaxfun.call_site,3000,1);
    });
    $("#click_menu_2").click(function() {
        $("#click_menu_1").attr('class','menu_container');
        $("#click_menu_2").attr('class','menu_container_selected');
        $("#click_menu_3").attr('class','menu_container');
        $("#click_menu_4").attr('class','menu_container');
        $("#title_select_menu").html('<a>관리자 위치</a>');

        Ajaxfun.call_site(2);
        clearInterval(timer);
        timer = setInterval(Ajaxfun.call_site,3000,2);
    });
    $("#click_menu_3").click(function() {
        $("#click_menu_1").attr('class','menu_container');
        $("#click_menu_2").attr('class','menu_container');
        $("#click_menu_3").attr('class','menu_container_selected');
        $("#click_menu_4").attr('class','menu_container');
        $("#title_select_menu").html('<a>에러 로그</a>');
        clearInterval(timer);
        Ajaxfun.call_site(3);
    });
    $("#click_menu_4").click(function() {
        $("#click_menu_1").attr('class','menu_container');
        $("#click_menu_2").attr('class','menu_container');
        $("#click_menu_3").attr('class','menu_container');
        $("#click_menu_4").attr('class','menu_container_selected');
        $("#title_select_menu").html('<a>직원 DB</a>');
        clearInterval(timer);
        Ajaxfun.call_site(4);
    });
    Ajaxfun={
        call_site:function (n){
            $.ajax({
                url: n+'/',
                data:{},
                success:function (data){
                    $("#menu_view").html(data).trigger("create")
                }
            });
        }
    }
    Ajaxfun.call_site(1);
    clearInterval(timer);
    timer = setInterval(Ajaxfun.call_site,3000,1);
})
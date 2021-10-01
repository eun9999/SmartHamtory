$(document).ready(function (){
    $('#user_add_bt').click(function () {
        var queryString =$('#form_sign').serialize();
        $.ajax({
            type : 'post',
            url : 'signup/',
            data : queryString,
            success : function (data){
                $("#menu_view").html(data);
            },
            error : function (xhr, status, error){
                alert(error);
            },
        })
    });
    $('#user_delete_bt').click(function (){
        var queryString =$('#form_user_delete').serialize();
        $.ajax({
            type: 'post',
            url : 'delete/',
            data : queryString,
            success : function (data){
                $("#menu_view").html(data);
            },
            error : function (xhr, status, error){
                alert(error);
            },
        });
    });
    $("#one_user_search_bt").click(function (){
        var queryString = $('#form_one_user_search').serialize();
        $.ajax({
            type: 'post',
            url : 'onesearch/',
            data : queryString,
            success : function (data) {
                $("#menu_view").html(data);
            },
            error : function (xhr, status, error){
                alert(error);
            },
        })
    })
    $('#user_all_search_bt').click(function (){
        $.ajax({
            url : 'allsearch/',
            data : {},
            success : function (data){
                $("#menu_view").html(data);
            },
            error : function (xhr, status, error){
                alert(error);
            },
        })
    })
})
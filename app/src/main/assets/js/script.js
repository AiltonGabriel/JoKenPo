$(document).ready(function(){
    $('.sidenav').sidenav();
    $('.my-container').css('height', `calc(100vh - ${$('#navbar').css('height')})`);

    $("#rockBtn").click(function(){
        self.removeClass('grey lighten-1');
        self.addClass('deep-purple darken-3');
        Android.playRock();
    });
});
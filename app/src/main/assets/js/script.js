let playerTurn = true;
$(document).ready(function(){

    $('.sidenav').sidenav();
    $('.my-container').css('height', `calc(100vh - ${$('#navbar').css('height')})`);

    Android.updateScoreboardGui();

    $("#rockBtn").click(function(){
        if(playerTurn){
            $("#rockBtn img").removeClass('grey lighten-1');
            $("#rockBtn img").addClass('deep-purple accent-2');

            playerTurn = false;
            $("#playAgainBtn").show();
            Android.playRock();
        }
    });

    $("#paperBtn").click(function(){
        if(playerTurn){
            $("#paperBtn img").removeClass('grey lighten-1');
            $("#paperBtn img").addClass('deep-purple accent-2');

            playerTurn = false;
            $("#playAgainBtn").show();
            Android.playPaper();
        }
    });

    $("#scissorsBtn").click(function(){
        if(playerTurn){
            $("#scissorsBtn img").removeClass('grey lighten-1');
            $("#scissorsBtn img").addClass('deep-purple accent-2');

            playerTurn = false;
            $("#playAgainBtn").show();
            Android.playScissors();
        }
    });

    $("#playAgainBtn").click(function(){
        Android.playAgain();
    });

    $("#resetScoreboarBtn").click(function(){
        Android.resetScoreboard();
    });

});

function playAgain(){
    $(".player-choice").each(function() {
        $(this).find('img').removeClass('deep-purple accent-2');
        $(this).find('img').addClass('grey lighten-1');
    });
    playerTurn = true;
    $("#playAgainBtn").hide();
    $("#computer-choice").attr("src", "img/covered_choice.png");
}

function setGameStatusLabel(text) {
    $("#game-status-label").text(text);
}

function setComputerChoice(computerChoice) {
    switch (computerChoice) {
        case 0:
            $("#computer-choice").attr("src", "img/ic_rock_192.png");
            break;
        case 1:
            $("#computer-choice").attr("src", "img/ic_paper_192.png");
            break;
        case 2:
            $("#computer-choice").attr("src", "img/ic_scissors_192.png")
            break;
    }
}

function setScoreboard(playerScore, computerScore) {
    $("#playerScore").text(playerScore);
    $("#computerScore").text(computerScore);
}
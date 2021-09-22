// mobile menu toggle – js pro hamburger
jQuery(document).ready(function() {
    jQuery("#nav-toggle").click(function() {
        jQuery(this).toggleClass("on");
        jQuery("#nav-main > ul").toggleClass("on");
    });
    
    // images preload – aby neproblikával hamburger
    promenna = new Image();
    promenna.src = window.basePath + "/css/images/hamburger.svg";
    promenna.src = window.basePath + "/css/images/hamburger-hover.svg";
    promenna.src = window.basePath + "/css/images/times.svg";
    promenna.src = window.basePath + "/css/images/times-hover.svg";
});

// smooth slide down to anchor – možná se využije i jinde než na homepage, jenomže mi nějak nefakčí te responzivní úprava
// if ($(window).width() < 640) {
//    $(document).ready(function() {
//        $('.jumbotron-large a').smoothScroll({offset: -50, speed: 1000});
//    });
// }
// else {
    $(document).ready(function() {
        $('.jumbotron-large a').smoothScroll({offset: -100, speed: 1000});
    });
//}

// html5 elements for old IE – jeden backport, aby se neřeklo
document.createElement('header');
document.createElement('section');
document.createElement('article');
document.createElement('aside');
document.createElement('nav');
document.createElement('footer');
document.createElement('figure');
document.createElement('figcaption');
document.createElement('main');
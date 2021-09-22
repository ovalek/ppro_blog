// mobile menu toggle – js pro hamburger
jQuery(document).ready(function() {
    jQuery("#nav-toggle").click(function() {
        jQuery(this).toggleClass("on");
        jQuery("#nav-main > ul").toggleClass("on");
    });
    
    // images preload – aby neproblikával hamburger
    var promenna = new Image();
    promenna.src = window.basePath + "/css/images/hamburger.svg";
    promenna.src = window.basePath + "/css/images/hamburger-hover.svg";
    promenna.src = window.basePath + "/css/images/times.svg";
    promenna.src = window.basePath + "/css/images/times-hover.svg";


    // náhodné natočení papírku s recenzemi
    $('.review').each(function() {
        var a = Math.random() * 2 - 1;
        $(this).css('transform', 'rotate(' + a + 'deg)');
    }, function() {
        $(this).css('transform', 'none');
    });

    // náhodné natočení náhledů fotek
    $('.album a').each(function() {
        var a = Math.random() * 2 - 1;
        $(this).css('transform', 'rotate(' + a + 'deg)');
    }, function() {
        $(this).css('transform', 'none');
    });


    // add class to links with img inside
    $('.article a:has(img), .editableArticle a:has(img)').addClass('imgLink');
});

$(document).ready(function() {

    $('.jumbotron-large').on('click', 'a', function(event) {
        var diff = 0;
        if (!Modernizr.touchevents) {
            diff = $('#header-main').height();
        }
        event.preventDefault();
        var $anchor = $('#kotva').first();
        if ($anchor.length) {
            $('html, body').animate({
                scrollTop: $anchor.offset().top - diff
            }, 1000);
        }
    });

    // animace top (jumbotron)
    $.extend($.expr[':'], {
        transparent: function (elem, i, attr) {
            return ( $(elem).css("opacity") === "0" );
        },
        opaque: function (elem, i, attr) {
            return ( $(elem).css("opacity") === "1" );
        }
    });

    var waitForSecond = true;
    var animationIntervalId;
    // preload first image (both variants)
    $('<img src="' + $('.photoAnimationContainer li.hidden-xs:first').attr('data-url') + '"\><img src="' + $('.photoAnimationContainer li.hidden-lg:first').attr('data-url') + '"\>')
        .load(function() {
            if (waitForSecond) {
                waitForSecond = false;
                return;
            }

            var $con = $('.photoAnimationContainer');

            $con.find('li').each(function() {
                $(this).css({
                    'background-image': 'url(\'' + $(this).attr('data-url') + '\')',
                    'opacity': '0.0',
                    'z-index': '1'
                });
            });

            $con.find('li:visible:first').fadeTo(2000, 1.0, function() {
                animation($con);
            });

            var oldWidth = $(window).width();

            $(window).on('resize', function(event) {
                // if only height differs, don't do anything
                if ($(window).width() == oldWidth) return;
                if ($('.photoAnimationContainer').find("li:visible").length <= 1) return;

                // stop animation
                clearInterval(animationIntervalId);

                $('.photoAnimationContainer li').removeClass('animate').css({"opacity": "0.0", "z-index":"1", "transform": "scale(1.0)"});
            });
            $(window).on('resizeEnd', function(event) {
                // if only height differs, don't do anything
                if ($(window).width() == oldWidth) return;
                oldWidth = $(window).width();

                // if ($('.photoAnimationContainer').find("li:visible").length <= 1) return;

                // stop animation
                clearInterval(animationIntervalId);

                $('.photoAnimationContainer li').removeClass('animate').css({"opacity": "0.0", "z-index":"1", "transform": "scale(1.0)"});

                // fadeIn first image again
                $('.photoAnimationContainer li:visible:first').fadeTo(2000, 1.0, function() {
                    // start animation again
                    animation($con);
                });
            })
        });

    var animation = function($con) {
        if ($con.find("li:visible").length <= 1) return;

        $con.find("li:visible").css("visibility", "visible");
        $con.find("li:visible:first").addClass('animate').css({"opacity": "1.0", "transform": "scale(1.15)"});
        $con.find("li:visible:not(:first)").removeClass('animate').css({"opacity": "0.0", "transform": "scale(1.0)"});

        if ($con.find("li:visible").length > 1) {
            var changePhoto = function () {
                $con.find("li:visible:opaque").css({"z-index": "2", "transform": "scale(1.0)"}).fadeTo(2000, 0.0, null, function() {
                    $(this).removeClass('animate');
                });
                // select next image
                if ($con.find("li:visible:opaque ~ li:visible:transparent:first").length) {
                    $con.find("li:visible:opaque ~ li:visible:transparent:first")
                        .addClass('animate')
                        .css({"z-index": "1", "transform": "scale(1.15)"})
                        .fadeTo(1000, 1.0);
                } else {
                    // select first image
                    $con.find("li:visible:transparent:first")
                        .addClass('animate')
                        .css({"z-index": "1", "transform": "scale(1.15)"})
                        .fadeTo(1000, 1.0);
                }
            };
            animationIntervalId = window.setInterval(function () {
                changePhoto();
            }, 9000);
        }
    };

});

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
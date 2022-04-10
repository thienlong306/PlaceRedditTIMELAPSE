var index = 0;
setInterval(() => {
    $("img").attr("src", `data/${listImg[++index].nameFile}`);
    if(index==listImg.length-1){
      index=0;
    }
}, 100);
var $loupe = $(".loupe"),
  loupeWidth = $loupe.width(),
  loupeHeight = $loupe.height();
var zoomLvl = 1;
$(document).on("mouseenter", ".image", function (e) {
  var $currImage = $(this),
    $img = $("<img/>")
      .attr("src", $("img", this).attr("src"))
      .css({ width: $currImage.width() * 2*zoomLvl, height: $currImage.height() * 2*zoomLvl });

  $loupe.html($img).fadeIn(100);

  $(document).on("mousemove", moveHandler);

  function moveHandler(e) {
    var imageOffset = $currImage.offset(),
      fx = imageOffset.left - loupeWidth / 2,
      fy = imageOffset.top - loupeHeight / 2,
      fh = imageOffset.top + $currImage.height() + loupeHeight / 2,
      fw = imageOffset.left + $currImage.width() + loupeWidth / 2;
    $loupe.css({
      left: e.pageX - 50,
      top: e.pageY - 50,
    });

    var loupeOffset = $loupe.offset(),
      lx = loupeOffset.left,
      ly = loupeOffset.top,
      bigy = (ly - (loupeHeight / 4)  - fy) * 2,
      bigx = (lx - (loupeWidth / 4)  - fx) * 2 ;
    $img.css({ 
      left: -bigx*zoomLvl, 
      top: -bigy*zoomLvl 
    });
    
  $(".image").mouseleave(function () { 
    console.log("object");
    $img.remove();
    $(document).off("mousemove", moveHandler);
    $loupe.fadeOut(100);
  });
  }
  $("img").on('mousewheel DOMMouseScroll', function(event){
    event.preventDefault();
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
      if(zoomLvl < 10) {
        zoomLvl += 0.5;
        $img.css({ width: $currImage.width() *2* zoomLvl, height: $currImage.height() *2* zoomLvl });
      }
    }
    else {
        if(zoomLvl > 1) {
          zoomLvl -= 0.5;
          $img.css({ width: $currImage.width() *2* zoomLvl, height: $currImage.height() *2* zoomLvl });
      }
    }
  });
});

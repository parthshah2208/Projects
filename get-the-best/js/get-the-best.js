var getTopTracks = function(name){

  var $countrycode = $(".country-select");

  $.ajax({
    url:"https://api.spotify.com/v1/search?q=" + name + "&type=artist",
    method:"GET"
  }).success(function(artistData){
    var artistId = artistData.artists.items[0].id;

    $.ajax({
      url:"https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country="+$countrycode.val(),
      method:"GET"
    }).success(function(topTracks){
      //this function expects an array
      //topTracks comes back as an object
      //with an array inside - watch out for that
      renderTopTracks(topTracks.tracks);
    });
  }).error(function(err){
    var parsedErr = JSON.parse(err.responseText);

    console.log(parsedErr.error.message,parsedErr.error.status);
  });
}

//function from underscore library
//returns a function that produces
//HTML based on a template
//.... WHAT?!
var trackTemplate = _.template($(".track-template").html());

//argument must be an array
var renderTopTracks = function(tracks){
  var $tracksElement = $("<div>");

  for(var i = 0; i < tracks.length; i++){
    var trackHTML = trackTemplate({
      title:tracks[i].name,
      artist:tracks[i].artists[0].name,
      album:tracks[i].album.name,
      songUrl:tracks[i].preview_url,
      albumUrl:tracks[i].album.external_urls.spotify,
      artistUrl:tracks[i].artists[0].external_urls.spotify
    });
    $tracksElement.append(trackHTML);
  }

  $(".top-tracks").html($tracksElement);
}

//default javascript object
var audio = document.createElement("audio");
$("body").append(audio);

$(".artist-search").on("keydown",function(event){
  if(event.which === 13){
    getTopTracks($(this).val());
    $(this).val("");
  }
});

//delegate event - because there are no
//elements with a class of "play" when the page loads
$(document).on("click",".play",function(){
  var songUrl = $(this).attr("url");

  $(audio).attr("src",songUrl);
  audio.play();
})

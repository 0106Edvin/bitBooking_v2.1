/**
 * Created by User on 9/29/2015.
 */
var geocoder = new google.maps.Geocoder();

function geocodePosition(pos) {
    geocoder.geocode({
        latLng: pos
    }, function(responses) {
        if (responses && responses.length > 0) {
            updateMarkerAddress(responses[0].formatted_address);
        } else {
            updateMarkerAddress('Cannot determine address at this location.');
        }
    });
}

function updateMarkerStatus(str) {
    document.getElementById('markerStatus').innerHTML = str;
}

function updateMarkerPosition(latLng) {
    document.getElementById('info').innerHTML = [
        latLng.lat(),
        latLng.lng()
    ].join(', ');
    document.getElementById('gLat').value = latLng.lat();
    document.getElementById('gLng').value = latLng.lng();
}

function updateMarkerAddress(str) {
    document.getElementById('address').innerHTML = str;
}

function initialize() {
    var latLng;
    var x = document.getElementById('gLat').value;
    var y = document.getElementById('gLng').value;

    latLng = new google.maps.LatLng(x, y);

    var map = new google.maps.Map(document.getElementById('mapCanvas'), {
        zoom: 12,
        center: latLng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });
    var marker = new google.maps.Marker({
        position: latLng,
        title: 'Click here to zoom',
        map: map,
        animation: google.maps.Animation.BOUNCE,
        draggable: false
    });

    google.maps.event.addListener(marker,'click',function() {
        map.setZoom(16);
        map.setCenter(marker.getPosition());
        infowindow.open(map,marker);
    });

    //create circle options
    var circleOptions = {
        fillColor: 'white',
        map: map,
        center: latLng,
        radius: 1000
    };

    //create circle
    myCircle = new google.maps.Circle(circleOptions);

    //when marker has completed the drag event
    //recenter the circle on the marker.
    google.maps.event.addListener(marker, 'dragend', function(){
        myCircle.setCenter(this.position);
    });

    // Update current position info.
    updateMarkerPosition(latLng);
    geocodePosition(latLng);

    // Add dragging event listeners.
    google.maps.event.addListener(marker, 'dragstart', function() {
        updateMarkerAddress('Dragging...');
    });

    google.maps.event.addListener(marker, 'drag', function() {
        updateMarkerStatus('Dragging...');
        updateMarkerPosition(marker.getPosition());
    });

    google.maps.event.addListener(marker, 'dragend', function() {
        updateMarkerStatus('Drag ended');
        geocodePosition(marker.getPosition());
    });
}

// Onload handler to fire off the app.
google.maps.event.addDomListener(window, 'load', initialize);
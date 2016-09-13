<script type="text/javascript">
    var stompClient = null;
    
    $(function() {
        $('#reservationsDiv').hide();
        
        $("#connect").click(function() {
            connect();
        })
        
        $("#disconnect").click(function() {
            disconnect();
        })          
    });

    function connect() {
        var socket = new SockJS('http://localhost:9080/reservations');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/updates', function(update){
                updateReservation(JSON.parse(update.body));
            });
        });
    }

    function disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function setConnected(connected) {
        $('#connect').prop('disabled',connected);
        $('#disconnect').prop('disabled',!connected);
        if (connected) {
            $('#reservationsDiv').show();
            
            $.get("http://localhost:9080/api/reservations", function(reservations) {
                console.log('Getting reservations');
                $.each(reservations, function(index, reservation) {
                    console.log('Reservation: ' + reservation);
                    updateReservation(reservation);
                });
            });
        } else {
            $('#reservationsDiv').hide();
        }
        $('#response').html('');            
    }

    function updateReservation(reservation) {        
        var response = $('#response');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(reservation.id 
                + ", " + reservation.name
                + ", " + reservation.server_name
                + ", " + reservation.start_date
                + ", " + reservation.end_date
                + ", " + reservation.approved));
        response.append(p);
    }        
</script>
<script src="/js/fullcalendar.min.js"></script>

<script type="text/javascript">
    var stompClient = null;
    
    $(document).ready(function() {
        $('#reservationsDiv').hide();
        
        $("#connect").click(function() {
            connect();
        })
        
        $("#disconnect").click(function() {
            disconnect();
        }) 

        $('#calendarDiv').fullCalendar({
            height: 450,
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,basicWeek,basicDay'
            },            
            defaultView: 'basicWeek',
            views: {
                agendaWeek: {
                    groupByResource: true
                },
                basicWeek: {
                    groupByResource: true
                }
            },
            editable: true,
            events: [{
                id: "event1",
                title: 'Server 1 - User 1',
                start: '2016-09-20T00:00:00.000Z',
                end: '2016-09-23T00:00:00.000Z',
                allDay: true,
                color: "orange",
                resources: ['server1']
            },{
                id: "event2",
                title: 'Server 2 - User 2',
                start: '2016-09-22T00:00:00.000Z',
                end: '2016-09-24T00:00:00.000Z',
                allDay: true,
                color: "blue",
                resourceEditable: true,
                resources: ['server2']
            },{
                id: "event3",
                title: 'Server 3 - User 3',
                start: '2016-09-23T00:00:00.000Z',
                end: '2016-09-24T00:00:00.000Z',
                allDay: true,
                color: "green",
                resources: ['server3']
            },{
                id: "event4",
                title: 'Server 4 - User 4',
                start: '2016-09-23T00:00:00.000Z',
                end: '2016-09-24T00:00:00.000Z',
                allDay: true,
                color: "red",
                resources: ['server4']
            }],
            eventDrop: function(event, delta, revertFunc) {updateServerReservation(event);
            },
            eventResize: function(event, delta, revertFunc) {updateServerReservation(event);
            },
            resourceColumns: [{
                labelText: 'Server',
                field: 'title',
                group: true
            }],
            resources: [
                { 
                    id: 'server1',
                    title: 'Server 1',
                },{ 
                    id: 'server2',
                    title: 'Server 2'
                },{ 
                    id: 'server3',
                    title: 'Server 3'
                },{ 
                    id: 'server4',
                    title: 'Server 4'
                }
            ]
        });         
    });

    function updateServerReservation(event) {
        confirm("Send Server Reservation update? " + event.title + " " + event.start.format() + " " + event.end.format());
    }

    function connect() {
        var socket = new SockJS('/microservices-websocket-stomp');
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
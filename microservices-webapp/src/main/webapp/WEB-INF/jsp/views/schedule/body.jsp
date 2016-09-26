<div class="container theme-showcase" role="main">
	<div class="page-header"></div>
	<div id="schedule" class="page-header">
        <h1>Welcome to the Lab Reservation System</h1>
	</div>
	<div class="panel panel-default">
		<div id="schedulerDiv" class="panel-header"></div>
		<div id="schedulerDiv" class="panel-body">
			<span style="padding-right:20px"><label style="padding-right:10px">Name:</label><select id="usersList"></select></span>
			<span style="padding-right:20px"><label style="padding-right:10px">Start Date:</label><nbsp;><input type="text" id="startDatepicker"></span>
			<span style="padding-right:20px"><label style="padding-right:10px">End Date:</label><nbsp;><input type="text" id="endDatepicker"></span>
			<span style="padding-right:20px"><label style="padding-right:10px">Server:</label><nbsp;><select id="serversList"></select></span>
			<button id="submitReservationBtn" type="button" class="btn btn-primary">Submit</button>
		</div>
	</div>
	<div>		
		<div id="calendarDiv"></div>
	</div>
	<div>		
		<div id="response"></div>
	</div>

</div>


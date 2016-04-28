

function nativeFeaturesAppController($scope, $compile, $location,$http,$timeout,$element) {
	
	$scope.name='Sanket';
	
		
		$scope.openContacts = function(){
			console.log("openContacts");
			
			//Contact.accessPhone();
			
			$http.post('rest/getMobileContacts?cd='+ new Date().getTime())
			.error(function(data){$scope.search=undefined;$location.path("/errorPage");})
			.success(function(data){
				console.log(data);
				$scope.mobileContactsData=data;
				sessionStorage.setItem("mobileContactsData", JSON.stringify($scope.mobileContactsData));
				$location.path("/mobileContactDetails");
			});
		};
		
		$scope.addToPhone = function(pocContactName,pocContactNumber){
			console.log("syncContacts");
			//$scope.number
			$scope.pocContactName=pocContactName;
			$scope.pocContactNumber=pocContactNumber;
			//Contact.syncContactsToPhone("Mehmood","9832746543");
			//Logout.logoutDialog();
			console.log("pocContactName --> "+pocContactName);
			console.log("pocContactNumber --> "+pocContactNumber);
			Contact.syncContactsToPhone(pocContactName,pocContactNumber);
			
			
		};
		
}


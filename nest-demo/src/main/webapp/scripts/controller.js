(function() {

	angular.module('nestExample').controller('appController',
			[ 'appService', AppController ]);

	function AppController(appService) {
		var vm = this;

		vm.minTemperature = 0;
		vm.temperature = 0;
		vm.maxTemperature = 0;
		vm.security;
				
		vm.paramChanged = function (){
			vm.message="";
		}

		vm.getParameters = function() {
			vm.minTemperature = appService.getMinTemperature().then(function (min) {
				vm.minTemperature = min;
				console.log(min);
            });
			vm.temperature = appService.getTemperature().then(function (temp) {
				vm.temperature = temp;
				console.log(temp);
            });
			vm.maxTemperature = appService.getMaxTemperature().then(function (max) {
				vm.maxTemperature = max;
				console.log(max);
            });
			vm.security = appService.getSecurity().then(function (security) {
				vm.security = security;
				console.log(security);
            });
			vm.paramChanged();
		};

		vm.getParameters();

		vm.setParameters = function() {
			appService.setMinTemperature(vm.minTemperature);
			appService.setTemperature(vm.temperature);
			appService.setMaxTemperature(vm.maxTemperature);
			appService.setSecurity(vm.security);
			
			vm.message="Updated";
		}
		
		vm.tempValid = function(){
			return ((vm.maxTemperature > vm.temperature) && (vm.temperature > vm.minTemperature));						
		}

	}
}());
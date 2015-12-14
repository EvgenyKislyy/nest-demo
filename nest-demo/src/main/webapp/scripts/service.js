(function() {

	angular.module('nestExample').factory('appService',
			[ '$http', appService ]);

	function appService($http) {
		return {
			getTemperature : getTemperature,
			setTemperature : setTemperature, // ---
			getMinTemperature : getMinTemperature,
			setMinTemperature : setMinTemperature, // ---
			getMaxTemperature : getMaxTemperature,
			setMaxTemperature : setMaxTemperature, // ---
			setSecurity : setSecurity,
			getSecurity : getSecurity
		// ---
		};

		function getTemperature() {
			var params = {};
			var config = {
				params : params
			};

			return $http.get('/WebService/temperature', config).then(
					returnRespData);
		}

		function setTemperature(temperature) {
			var params = {
				t : temperature
			};
			var config = {
				params : params
			};

			return $http.post('/WebService/temperature', null, config).then(
					returnRespData);
		}

		function getMinTemperature() {
			var params = {};
			var config = {
				params : params
			};

			return $http.get('/WebService/min_temperature', config).then(
					returnRespData);
		}

		function setMinTemperature(temperature) {
			var params = {
				t : temperature
			};
			var config = {
				params : params
			};

			return $http.post('/WebService/min_temperature', null, config)
					.then(returnRespData);
		}
		function getMaxTemperature() {
			var params = {};
			var config = {
				params : params
			};

			return $http.get('/WebService/max_temperature', config).then(
					returnRespData);
		}

		function setMaxTemperature(temperature) {
			var params = {
				t : temperature
			};
			var config = {
				params : params
			};

			return $http.post('/WebService/max_temperature', null, config)
					.then(returnRespData);
		}
		function getSecurity() {
			var params = {};
			var config = {
				params : params
			};

			return $http.get('/WebService/security', config).then(
					returnRespData);
		}

		function setSecurity(security) {
			var params = {
				s : security
			};
			var config = {
				params : params
			};

			return $http.post('/WebService/security', null, config).then(
					returnRespData);
		}

		function returnRespData(resp) {
			return resp.data;
		}

	}

}());
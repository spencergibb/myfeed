angular.module('sso', [ 'ngRoute', 'ngResource' ]).config(
		function($routeProvider) {

			$routeProvider.otherwise('/');
			$routeProvider.when('/', {
				templateUrl : 'home.html',
				controller : 'home'
			}).when('/dashboard', {
				templateUrl : 'dashboard.html',
				controller : 'dashboard'
			});

}).service('userService', function($http) {
        this.user = $http.get('/dashboard/user');
        this.getUser = function() {
            return this.user;
        }
}).controller('navigation', function($scope, $http, $window, $route, userService) {
	$scope.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};
	if (!$scope.user) {
        userService.getUser().then(function(user) {
            console.log("user: %o", user);
			$scope.user = user.data.principal;
			$scope.authenticated = true;
		}, function() {
            $scope.user = null;
			$scope.authenticated = false;
		});
	}
	$scope.logout = function() {
		$http.post('/dashboard/logout', {}).success(function() {
			delete $scope.user;
			$scope.authenticated = false;
			// Force reload of home page to reset all state after logout
			$window.location.hash = '';
		});
	};
}).controller('home', function($scope, $resource, userService) {
        userService.getUser().then(function(user) {
            console.log("user: %o", user);
            $resource("/feed/@"+user.data.principal, {}).get({}, function(feed) {
                $scope.feed = feed;
            }, function(err) {
                console.log("Error getting feed: "+err);
            });
            $scope.user = user;
            $scope.authenticated = true;
        }, function(err) {
            console.log("Error getting user: "+err);
            $scope.user = null;
            $scope.authenticated = false;
        });
}).controller('dashboard', function($scope, $resource) {

	$resource('/dashboard/message', {}).get({}, function(data) {
		$scope.message = data.message;
	}, function() {
		$scope.message = '';
	});

});
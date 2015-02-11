angular.module('sso', [ 'ngRoute', 'ngResource' ]).config(
		function($routeProvider, $locationProvider) {
            $locationProvider.html5Mode(true)

			$routeProvider.otherwise('/');
            $routeProvider.when('/@:username', {
                templateUrl : 'feed.html',
                controller : 'feed'
			}).when('/', {
				templateUrl : 'feed.html',
				controller : 'feed'
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
            delete $scope.feed;
			$scope.authenticated = false;
			// Force reload of home page to reset all state after logout
			$window.location.hash = '';
		});
	};
}).controller('feed', function($scope, $resource, $route, userService) {
        console.log("feed route.current.params.username: %o", $route.current.params.username)
        userService.getUser().then(function(user) {
            console.log("user: %o", user);
            var feeduser = user.data.principal;

            if ($route.current.params.username) {
                feeduser = $route.current.params.username;
            }

            if (feeduser) {
                $scope.feeduser = feeduser;

                $resource("/feed/@" + feeduser, {}).get({}, function (feed) {
                    $scope.feed = feed;
                }, function (err) {
                    console.log("Error getting feed: " + err);
                });

                $scope.user = user;
                $scope.authenticated = true;
            } else {
                $scope.feeduser = null;
                $scope.feed = null;
                $scope.user = null;
                $scope.authenticated = false;
            }
        }, function(err) {
            console.log("Error getting user: "+err);
            $scope.feeduser = null;
            $scope.feed = null;
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
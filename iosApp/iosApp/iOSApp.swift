import SwiftUI
import Mixpanel

@main
struct iOSApp: App {
    
    init() {
        Mixpanel.initialize(token: "b1d5596551e4386eece99f448efbfd2b", trackAutomaticEvents: true)
        Mixpanel.mainInstance().useIPAddressForGeoLocation = false
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.all)
        }
    }
}

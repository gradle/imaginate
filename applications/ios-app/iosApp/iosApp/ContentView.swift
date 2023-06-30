import UIKit
import SwiftUI
import imaginate_ios_app

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        IosMainKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {

    var body: some View {
        // Compose has own keyboard handler
        ComposeView().ignoresSafeArea(.keyboard)
    }
}

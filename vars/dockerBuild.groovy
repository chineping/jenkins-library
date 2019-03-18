/*
Function to build and push a docker image. Applies all given tags to the image.
*/
def call(String imageName, ArrayList tags, String context) {
    stage(imageName) {
        dir(context) {
            image = docker.build(imageName)
            for (tag in tags) {
                image.push(tag)
            }
        }
    }
}
<template>
    <div>
        <span>chat on line 13444</span>
        <video style="width: 500px;height: 500px;" autoplay playsinline></video>
    </div>
</template>

<script>
    /**
     * Illustrates how to clone and manipulate MediaStream objects.
     */




    export default {
        name: "rtcc",
        data() {
            return {
                constraints: {
                    audio: true,
                    video: true,
                }
            }
        },
        methods: {
            hasUserMedia() {
                //check if the browser supports the WebRTC
                return !!(navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia);
            },

            makeVideoOnlyStreamFromExistingStream(stream) {
                let videoStream = stream.clone();
                let audioTracks = videoStream.getAudioTracks();
                for (let i = 0, len = audioTracks.length; i < len; i++) {
                    videoStream.removeTrack(audioTracks[i]);
                }
                console.log('created video only stream, original stream tracks: ', stream.getTracks());
                console.log('created video only stream, new stream tracks: ', videoStream.getTracks());
                return videoStream;
            }
            , makeAudioOnlyStreamFromExistingStream(stream) {
                let audioStream = stream.clone();
                let videoTracks = audioStream.getVideoTracks();
                for (let i = 0, len = videoTracks.length; i < len; i++) {
                    audioStream.removeTrack(videoTracks[i]);
                }
                console.log('created audio only stream, original stream tracks: ', stream.getTracks());
                console.log('created audio only stream, new stream tracks: ', audioStream.getTracks());
                return audioStream;
            }
        }, mounted() {

            navigator.mediaDevices.getUserMedia(this.constraints).then((stream) => {
                let video = document.querySelector("video");


                //  let audioOnlyStream = this.makeAudioOnlyStreamFromExistingStream(stream);
                let videoOnlyStream = this.makeVideoOnlyStreamFromExistingStream(stream);


                video.srcObject = videoOnlyStream
            }).catch((error) => {
                console.error('getUserMedia() error: ', error);
            });


        }
    }
</script>

<style scoped>

</style>
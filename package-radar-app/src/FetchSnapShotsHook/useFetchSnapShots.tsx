import { useState } from "react";
import { SnapShotsTimelineSchema, type SnapShotTimeline } from "./SnapShot";

function useFetchSnapShots(){
    const [snapShotsLoading, setSnapShotsLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [fetchedSnapShots, setFetchedSnapShots] = useState<SnapShotTimeline|null>(null);

    async function getSnapShots(packageId: string|null|undefined){

        if(!packageId){
            return;
        }
        setSnapShotsLoading(true);

        try{
            let response = null;
            response = await fetch(`/bend/api/package/${packageId}/snapshots`);
            if(response.status == 200){
                console.log("Response was 200");
                const data = await response.json();
                console.log("Raw snapshot data:", data);
                console.log("ABOUT TO PARSE");
                const snapshots = SnapShotsTimelineSchema.parse(data);
                console.log("Parsed snapshots:", snapshots);
                console.log("FINISHED PARSE");
                setFetchedSnapShots(snapshots);

                console.log("setFetchedSnapShots called");
            }
            else{
                setFetchError("Response was empty");
            }
        }
        catch(responseError: unknown){
            console.log("Snapshot request threw an error:", responseError);
            
            if(responseError instanceof Error){
                setFetchError("Backend Error: " + responseError);
            }
            else{
                setFetchError("Unkown Error");
            }
        }
        setSnapShotsLoading(false);
    }

    return {getSnapShots, snapShotsLoading, fetchError, fetchedSnapShots}
}

export default useFetchSnapShots;
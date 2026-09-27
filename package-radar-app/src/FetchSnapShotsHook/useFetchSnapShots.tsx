import { useState } from "react";
import { SnapShotsTimelineSchema, type SnapShotTimeline } from "./SnapShot";

function useFetchSnapShots(signal: AbortSignal|null|undefined){
    const [snapShotsLoading, setSnapShotsLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [fetchedSnapShots, setFetchedSnapShots] = useState<SnapShotTimeline|null>(null);

    async function getSnapShots(packageId: string|null|undefined){

        if(!packageId || signal == null || signal == undefined){
            return;
        }
        setSnapShotsLoading(true);

        try{
            let response = null;
            response = await fetch(`/bend/api/package/${packageId}/snapshots`, {signal,});
            if(signal?.aborted){
                return;
            }
            if(response.status == 200){
                const data = await response.json();
                const snapshots = SnapShotsTimelineSchema.parse(data);
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
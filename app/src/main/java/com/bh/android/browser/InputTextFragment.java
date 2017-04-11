package com.bh.android.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputTextFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mEditText = null;

    private OnFragmentInteractionListener mListener;

    public InputTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputTextFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputTextFragment newInstance(String param1, String param2) {
        InputTextFragment fragment = new InputTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void hideIME(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_text, container, false);
        mEditText = (EditText) view.findViewById(R.id.text_field);


        Button mBtnInject = (Button) view.findViewById(R.id.btn_inject);
        mBtnInject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideIME(v);
                onJavaScriptInjection(mEditText.getText().toString());
            }
        });
        Button mBtnNoInject = (Button) view.findViewById(R.id.btn_no_inject);
        mBtnNoInject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideIME(v);
                onCancelPressed();
            }
        });

        return view;
    }


    public void onJavaScriptInjection(String injectionString) {
        if (mListener != null) {
            mListener.onJavaScriptInjection(injectionString);
        }
    }

    public void onCancelPressed() {
        if (mListener != null) {
            mListener.onCancelPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onJavaScriptInjection(String injectionString);

        void onCancelPressed();
    }
}

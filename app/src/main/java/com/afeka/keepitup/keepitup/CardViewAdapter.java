package com.afeka.keepitup.keepitup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Filter;
import android.widget.TextView;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import static android.support.v7.widget.helper.ItemTouchHelper.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private static final String ID_BUNDLE = "ID";
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Transaction> cardsToShow;
    private ArrayList<Transaction> originalArray;

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView nameTextView;
        private TextView startDateTextView;
        private TextView company;

        public ViewHolder(View itemView){
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameToShow);
            startDateTextView = itemView.findViewById(R.id.startDateToShow);
            company = itemView.findViewById(R.id.companyToShow);
        }
    }

    public CardViewAdapter(Context context, ArrayList<Transaction> list) {
        this.context = context;
        this.cardsToShow = list;
        originalArray = cardsToShow;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.from(parent.getContext())
                .inflate(R.layout.my_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Transaction currentTrans = cardsToShow.get(position);
        CardView cardView = holder.itemView.findViewById(R.id.card_view);

        if(currentTrans.isExpired())
            cardView.setBackgroundResource(R.drawable.roundexpired);
        else
            cardView.setBackgroundResource(R.drawable.roundcard);


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        holder.nameTextView.setText(currentTrans.getName());
        holder.startDateTextView.setText(df.format(currentTrans.getStartDate()));
        holder.company.setText(currentTrans.getCompany());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(ID_BUNDLE ,cardsToShow.get(position).getId());
                TransactionShowFragment transactionShowFragment = new TransactionShowFragment();
                transactionShowFragment.setArguments(bundle);
                ((MenuActivity)context).replaceFragment(transactionShowFragment);
            }
        });
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                FilterResults filterResults = new FilterResults();
                ArrayList<Transaction> filteredList = new ArrayList();
                if(query.length() == 0) {
                    filteredList = originalArray;
                }else {
                    for (int i = 0; i < cardsToShow.size(); i++) {
                        if (cardsToShow.get(i).getName().toLowerCase().contains(query)) {
                            filteredList.add(cardsToShow.get(i));
                        }
                    }
                }

                filterResults.count = filteredList.size();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cardsToShow = (ArrayList<Transaction>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return cardsToShow.size() ;
    }

    }

   class SwipeCardController extends Callback {

       enum ButtonsState {
           GONE,
           LEFT_VISIBLE,
           RIGHT_VISIBLE
       }

       private Boolean isSwipeBack = false;
       private ButtonsState buttonState = ButtonsState.GONE;
       private static final float buttonWidth = 200;
       private RectF buttonInstance = null;
       private RecyclerView.ViewHolder currentItemViewHolder = null;
       private SwipeControllerActions buttonsActions = null;
       private Context context;


       public SwipeCardController(Context context , SwipeControllerActions buttonsActions) {
           this.buttonsActions = buttonsActions;
           this.context = context;
       }

       @Override
       public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
           return makeMovementFlags(0, LEFT | RIGHT);
       }

       @Override
       public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
           return false;
       }

       @Override
       public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

       }

       @Override
       public int convertToAbsoluteDirection(int flags, int layoutDirection) {
           if (isSwipeBack) {
               if(buttonState != ButtonsState.GONE)
               isSwipeBack = true;
               else
                   isSwipeBack = false;
               return 0;
           }
           return super.convertToAbsoluteDirection(flags, layoutDirection);
       }

       @Override
       public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
           if (actionState == ACTION_STATE_SWIPE) {
               if (buttonState != ButtonsState.GONE) {
                   if (buttonState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
                   if (buttonState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
                   super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
               }
               else {
                   setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
               }
           }

           if (buttonState == ButtonsState.GONE) {
               super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
           }
           currentItemViewHolder = viewHolder;
       }

       private void setTouchDownListener(final Canvas c,
                                         final RecyclerView recyclerView,
                                         final RecyclerView.ViewHolder viewHolder,
                                         final float dX, final float dY,
                                         final int actionState, final boolean isCurrentlyActive) {
           recyclerView.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   if (event.getAction() == MotionEvent.ACTION_DOWN) {
                       setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                   }
                   return false;
               }
           });
       }

       private void setTouchListener(final Canvas c,
                                     final RecyclerView recyclerView,
                                     final RecyclerView.ViewHolder viewHolder,
                                     final float dX, final float dY,
                                     final int actionState, final boolean isCurrentlyActive) {

           recyclerView.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   if( event.getAction() == MotionEvent.ACTION_CANCEL ||
                           event.getAction() == MotionEvent.ACTION_UP)
                       isSwipeBack = true;

                   if (isSwipeBack) {
                       if (dX < -buttonWidth) buttonState = ButtonsState.RIGHT_VISIBLE;
                       else if (dX > buttonWidth) buttonState = ButtonsState.LEFT_VISIBLE;

                       if (buttonState != ButtonsState.GONE) {
                           setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                           setItemsClickable(recyclerView, false);
                       }
                   }
                       return false;
                   }
               });
           }

       private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
           recyclerView.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                   if (event.getAction() == MotionEvent.ACTION_UP) {
                       SwipeCardController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                       recyclerView.setOnTouchListener(new View.OnTouchListener() {
                           @Override
                           public boolean onTouch(View v, MotionEvent event) {
                               return false;
                           }
                       });
                       setItemsClickable(recyclerView, true);
                       isSwipeBack = false;

                       if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
                           if (buttonState == ButtonsState.LEFT_VISIBLE) {
                               buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                           }
                           else if (buttonState == ButtonsState.RIGHT_VISIBLE) {
                               buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                           }
                       }
                       buttonState = ButtonsState.GONE;
                       currentItemViewHolder = null;
                   }
                   return false;
               }
           });
       }

       private void setItemsClickable(RecyclerView recyclerView,
                                      boolean isClickable) {
           for (int i = 0; i < recyclerView.getChildCount(); ++i) {
               recyclerView.getChildAt(i).setClickable(isClickable);
           }
       }


       private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
           float buttonWidthWithoutPadding = buttonWidth - 20 ;
           float corners = 20;

           View itemView = viewHolder.itemView;
           Paint paint = new Paint();

           RectF leftButton = new RectF(itemView.getLeft() , itemView.getTop()  , itemView.getLeft() + buttonWidthWithoutPadding  , itemView.getBottom());
           paint.setColor(context.getResources().getColor(R.color.edit_green));
           c.drawRoundRect(leftButton, corners, corners, paint);
           String textToShow = context.getResources().getString(R.string.edit);
           drawText(textToShow, c, leftButton, paint);

           RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding , itemView.getTop(), itemView.getRight() , itemView.getBottom());
           paint.setColor(context.getResources().getColor(R.color.delete_red));
           c.drawRoundRect(rightButton, corners, corners, paint);
           textToShow = context.getResources().getString(R.string.delete);
           drawText(textToShow, c, rightButton, paint);

           buttonInstance = null;
           if (buttonState == ButtonsState.LEFT_VISIBLE) {
               buttonInstance = leftButton;
           }
           else if (buttonState == ButtonsState.RIGHT_VISIBLE) {
               buttonInstance = rightButton;
           }
       }

       private void drawText(String text, Canvas c, RectF button, Paint paint) {
           float textSize = 40;
           paint.setColor(Color.WHITE);
           paint.setAntiAlias(true);
           paint.setTextSize(textSize);

           float textWidth = paint.measureText(text);
           c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), paint);
       }

       public void onDraw(Canvas c) {
           if (currentItemViewHolder != null) {
               drawButtons(c, currentItemViewHolder);
           }
       }
   }

 abstract class SwipeControllerActions {

    public void onLeftClicked(int position) {}

    public void onRightClicked(int position) {}

}